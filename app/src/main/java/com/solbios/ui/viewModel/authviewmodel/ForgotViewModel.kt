package com.solbios.ui.viewModel.authviewmodel

import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.solbios.R
import com.solbios.network.ApiState
import com.solbios.other.Constants
import com.solbios.ui.fragment.forgot.ForgotFragmentDirections
import com.solbios.ui.fragment.forgot.ForgotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ForgotViewModel @Inject constructor(val forgotRepository: ForgotRepository):ViewModel() {

    var mobile:String?=null
    var progressVisibility = ObservableField(false)
    var textVisibility = ObservableField(false)
    var validation = MutableLiveData<String>()

    val errorThrow=MutableLiveData<String>()

    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)

    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }



    fun forgotPassword(view: View){
        if (!isValidMobile(mobile)){
            validation.value= Constants.mobileNumber
        }
        else{
            getForgotPassword(view)
        }

    }


    fun getForgotPassword(view:View){
        viewModelScope.launch {
          forgotRepository.forgot(mobile).onStart {
              progressVisibility.set(true)
              _apiState.value=ApiState.Loading

          }.catch { e->
              val error= (e as? HttpException)?.response()?.errorBody()?.string()
              var obj = JSONObject(error)
              var name= obj["message"]
              _apiState.value = ApiState.Failure(e)
              progressVisibility.set(false)
              errorThrow.value=name.toString()

          }.collect {
              progressVisibility.set(false)

              _apiState.value=ApiState.Success(it.data)
              val s: String = java.lang.String.valueOf(it.data?.user_id)
              actionOnClick(s,view)



          }
        }
    }

    private fun actionOnClick(s: String, view: View) {
        val action=ForgotFragmentDirections.actionForgotFragmentToOtpFragment(s,"forgot")
        view.findNavController().navigate(action)
    }

    private fun isValidMobile(mobile:String?):Boolean{
        return mobile?.length==10 && !TextUtils.isEmpty(mobile)
    }
}

