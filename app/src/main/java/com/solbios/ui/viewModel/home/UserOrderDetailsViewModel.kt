package com.solbios.ui.viewModel.home

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solbios.network.ApiState
import com.solbios.ui.fragment.cart.userOrder.UserOrderDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserOrderDetailsViewModel @Inject constructor( val userOrderDetailsRepository: UserOrderDetailsRepository): ViewModel(){


    var progressVisibility = ObservableField(false)
    var noProduct=ObservableField(false)
    var noYourOrder=ObservableField(false)

    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }

    fun getUserOrder(header:String,page:Int?){
        viewModelScope.launch {
            userOrderDetailsRepository.getUserDetails(header,page).onStart {
                _apiState.value=ApiState.Loading
                progressVisibility.set(true)

            }.catch { e->
                _apiState.value=ApiState.Failure(e)
                progressVisibility.set(false)

            }.collect {
                _apiState.value=ApiState.Success(it)
                progressVisibility.set(false)
                if (it.total==0){
                    noProduct.set(true)
                    noYourOrder.set(false)
                }
                else{
                    noProduct.set(false)
                    noYourOrder.set(true)
                }
            }
        }
    }
}