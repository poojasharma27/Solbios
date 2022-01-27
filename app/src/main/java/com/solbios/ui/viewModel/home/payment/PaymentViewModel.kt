package com.solbios.ui.viewModel.home.payment

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.solbios.R
import com.solbios.db.entities.CouponDetails
import com.solbios.model.cart.applycoupon.CouponData
import com.solbios.model.paymentCreateOrder.CreateOrderIdRoot
import com.solbios.network.ApiState
import com.solbios.ui.fragment.cart.payment.PaymentFragmentDirections
import com.solbios.ui.fragment.cart.payment.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class PaymentViewModel @Inject constructor( val repository: PaymentRepository,val stateHandle: SavedStateHandle):ViewModel() {
    var progressVisibility = ObservableField(false)

    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }
    private val _apiStateOrderId= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiStateOrderId: StateFlow<ApiState> by lazy {
        _apiStateOrderId
    }
    var couponCode=stateHandle.get<CouponData>("couponCode")

    fun getOrderId(header:String?,amount:String?,taxAmount:String?){
        viewModelScope.launch {
            repository.getOrderId(header,amount,taxAmount).onStart {
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

    fun createOrderId(view: View, header: String?, amount: String?, addressId:Int?, orderId:String?, paymentType:Int?, transactionId:String?,status:Int?,reason:String?,taxAmount:String?){
        viewModelScope.launch {
            repository.createOrderId(header,amount,addressId,orderId,paymentType,transactionId,status,reason,taxAmount,couponCode?.coupon_id.toString(),couponCode?.discount_amount).onStart {
                _apiStateOrderId.value=ApiState.Loading
                progressVisibility.set(true)

            }.catch { e->
                _apiStateOrderId.value=ApiState.Failure(e)
                progressVisibility.set(true)


            }.collect {
               _apiStateOrderId.value=ApiState.Success(it)
                if (status==1) {
                    paymentSuccess(view, it)
                    progressVisibility.set(true)

                }else{
                   paymentFailure(view)
                    progressVisibility.set(true)

                }
            }
        }
    }

    private fun paymentFailure(view: View) {
   Navigation.findNavController(view).navigate(R.id.action_paymentFragment_to_paymentFailureFragment)
    }

    private fun paymentSuccess(view: View, it: CreateOrderIdRoot) {
        Navigation.findNavController(view).navigate(PaymentFragmentDirections.actionPaymentFragmentToPaymentSuccessFragment(it.data.order_id))
    }


}