package com.solbios.ui.viewModel.home.cart

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.solbios.R
import com.solbios.model.cart.applycoupon.CouponData
import com.solbios.network.ApiState
import com.solbios.ui.fragment.cart.applyCoupon.ApplyCouponFragmentDirections
import com.solbios.ui.fragment.cart.applyCoupon.ApplyCouponRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ApplyCouponViewModel @Inject constructor(val repository: ApplyCouponRepository,val stateHandle: SavedStateHandle) :ViewModel() {
    val totalAmount=stateHandle.get<Int>("toBePaid")
    var progressVisibility = ObservableField(false)
    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }
    private val _apiStateCoupon= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiStateCoupon: StateFlow<ApiState> by lazy {
        _apiStateCoupon
    }
    val errorThrow= MutableLiveData<String>()

     fun getApplyCoupon(header:String){
         viewModelScope.launch {
             repository.getApplyCoupon(header).onStart {

                 _apiState.value=ApiState.Loading
                 progressVisibility.set(true)

             }.catch { e->
                 _apiState.value=ApiState.Failure(e)
                 progressVisibility.set(false)

             }.collect {
                 _apiState.value=ApiState.Success(it)
                 progressVisibility.set(false)


             }
         }
     }

    fun getCoupon(view: View, header:String?, couponCode:String?){
        viewModelScope.launch {
            repository.getCoupon(header,totalAmount.toString(),couponCode).onStart {
                _apiStateCoupon.value=ApiState.Loading

                progressVisibility.set(true)

            }.catch { e->
                val error= (e as? HttpException)?.response()?.errorBody()?.string()
                var obj = JSONObject(error)
                var name= obj["message"]
                _apiStateCoupon.value=ApiState.Failure(e)
                errorThrow.value=name.toString()
                progressVisibility.set(false)



            }.collect {
                _apiStateCoupon.value=ApiState.Success(it)
                progressVisibility.set(false)

               applyCouponCart(view,it.data)

            }
        }
    }

    private fun applyCouponCart(view: View, it:CouponData) {
        Navigation.findNavController(view).navigate(ApplyCouponFragmentDirections.actionApplyCouponFragmentToCartFragment(it))

    }



}