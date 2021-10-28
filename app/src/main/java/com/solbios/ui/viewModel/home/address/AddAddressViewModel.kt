package com.solbios.ui.viewModel.home.address


import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.solbios.R
import com.solbios.network.ApiState
import com.solbios.other.Constants
import com.solbios.ui.fragment.cart.address.AddAddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(val repository: AddAddressRepository, val stateHandle: SavedStateHandle) : ViewModel() {
    val data = stateHandle.get<String>("Id")
    var fullName:String?=null
    var home:String?=null
    var mobile:String?=null
    var pinCode:String?=null
    var city:String?=null
    var state:String?=null
    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)
    var progressVisibility = ObservableField(false)
    val errorThrow= MutableLiveData<String>()
    var  validation =MutableLiveData<String>()
  //  val addAddress = ObservableField<Data>()
    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }
    private val _apiStateDetails= MutableStateFlow<ApiState>(ApiState.Empty)

    val apiStateDetails: StateFlow<ApiState> by lazy {
        _apiStateDetails
    }
  /*  val data = stateHandle.get<Data>("Data")

    fun update(){
        if (addAddress==null){

        }else{
            fullName =  data?.name23


        }
    }*/

    fun setAddress(view: View,header:String?,addressType:Int?){
        if(TextUtils.isEmpty(fullName)){
            validation.value= Constants.name
        }
        else if(TextUtils.isEmpty(home) ){
            validation.value= Constants.home

        } else if (!isValidMobile(mobile)){
            validation.value= Constants.mobileNumber
        }else if(TextUtils.isEmpty(pinCode) ){
            validation.value= Constants.pinCode
        }else if(TextUtils.isEmpty(city) ){
            validation.value= Constants.city
        }
        else if(TextUtils.isEmpty(state) ){
            validation.value= Constants.state
        }
        else{
           addAddress(view,header,addressType)
        }
    }

    fun addAddress(view:View,header:String?,addressType:Int?){
     viewModelScope.launch {
         repository.addAddress(header,home,fullName,state,city,pinCode,addressType,mobile,data).onStart {
             _apiState.value=ApiState.Loading
             progressVisibility.set(true)


         }.catch {e->
             progressVisibility.set(false)
             val error= (e as? HttpException)?.response()?.errorBody()?.string()
             var obj = JSONObject(error)
             var name= obj["message"]
             _apiState.value=ApiState.Failure(e)
             errorThrow.value=name.toString()


         }.collect {
             _apiState.value=ApiState.Success(it)
             progressVisibility.set(false)
            setDestination(view)

         }
     }



    }
    private fun isValidMobile(mobile:String?):Boolean{
        return mobile?.length==10 && !TextUtils.isEmpty(mobile)
    }
    private fun setDestination(it: View) {
        Navigation.findNavController(it).navigate(R.id.action_addAddressFragment_to_selectAddressFragment)
    }


    fun userAddressDetails(header:String?){
        viewModelScope.launch {
            repository.addAddressWithEdit(header,data).onStart {
                progressVisibility.set(true)
                _apiStateDetails.value=ApiState.Loading
            }.catch { e->
                progressVisibility.set(false)
                _apiStateDetails.value=ApiState.Failure(e)

            }.collect {
                progressVisibility.set(false)
                _apiStateDetails.value=ApiState.Success(it)

            }
        }
    }
}