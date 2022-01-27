package com.solbios.ui.viewModel.home.orderSummary

import androidx.databinding.ObservableField
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solbios.db.entities.CouponDetails
import com.solbios.model.cart.applycoupon.CouponData
import com.solbios.network.ApiState
import com.solbios.ui.fragment.cart.orderSummary.OrderSummaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderSummaryViewModel @Inject constructor(val repository: OrderSummaryRepository, val stateHandle: SavedStateHandle): ViewModel() {

   val id = stateHandle.get<Int>("id")
    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }
    var progressVisibility = ObservableField(false)
    var screenVisibility = ObservableField(false)
    var couponCode=stateHandle.get<CouponData>("couponCode")


    fun orderSummary(header:String){
     viewModelScope.launch {
         repository.orderSummary(header,id).onStart {
             progressVisibility.set(true)
             _apiState.value=ApiState.Loading
             screenVisibility.set(false)

         }.catch {e->
             progressVisibility.set(false)
             _apiState.value=ApiState.Failure(e)
             screenVisibility.set(true)


         }.collect {
             progressVisibility.set(false)
             _apiState.value=ApiState.Success(it)
             screenVisibility.set(true)


         }
     }
    }


}