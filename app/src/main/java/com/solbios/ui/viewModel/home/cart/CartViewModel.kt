package com.solbios.ui.viewModel.home.cart

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.solbios.R
import com.solbios.model.cart.applycoupon.CouponData
import com.solbios.network.ApiState
import com.solbios.ui.fragment.cart.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(val repository: CartRepository ,val stateHandle: SavedStateHandle) : ViewModel() {
    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)
    var progressVisibility = ObservableField(false)
    var screenVisibility = ObservableField(false)
    var noCartItemVisibility = ObservableField(false)
    var couponCode=stateHandle.get<CouponData>("couponCode")


    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }
    private val _apiStateAddtoCart= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiStateAddToCart: StateFlow<ApiState> by lazy {
        _apiStateAddtoCart
    }
    private val _apiStateDelete= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiStateDelete: StateFlow<ApiState> by lazy {
        _apiStateDelete
    }

    fun getCartItem(header:String?){
        viewModelScope.launch {
            repository.getCartList(header).onStart {
                _apiState.value=ApiState.Loading
                progressVisibility.set(true)
                screenVisibility.set(false)
            }.catch { e->
                _apiState.value=ApiState.Failure(e)
                progressVisibility.set(false)
                screenVisibility.set(true)

            }.collect {

                _apiState.value=ApiState.Success(it)
                progressVisibility.set(false)
                if (it.total==0){
                    noCartItemVisibility.set(true)
                }else{
                    screenVisibility.set(true)
                }

            }
        }
    }


    fun deleteCartItem(header: String?,id:Int?){
        viewModelScope.launch {
            repository.deleteCartItem(header,id).onStart {
                _apiStateDelete.value = ApiState.Loading
                progressVisibility.set(true)
                screenVisibility.set(false)

            }.catch {e->
                _apiStateDelete.value = ApiState.Failure (e)
                progressVisibility.set(false)
                screenVisibility.set(true)


            }.collect {
                _apiStateDelete.value=ApiState.Success(it)
                progressVisibility.set(false)
                if (it.data.cart_total==0){
                    noCartItemVisibility.set(true)
                }else{
                    screenVisibility.set(true)
                }
            }
        }
    }
    fun getAddToCart(header:String?,productId:Int?,action:Int?){
        viewModelScope.launch {
            repository.getAddToCart(header,productId,action).onStart {
                _apiStateAddtoCart.value = ApiState.Loading
                progressVisibility.set(true)

            }.catch {e->
                _apiStateAddtoCart.value = ApiState.Failure (e)
                progressVisibility.set(false)

            }.collect {

                _apiStateAddtoCart.value=ApiState.Success(it)
                progressVisibility.set(false)


            }
        }
    }

    fun productList(view: View){
        Navigation.findNavController(view).navigate(R.id.action_cartFragment_to_productListFragment)
    }
}