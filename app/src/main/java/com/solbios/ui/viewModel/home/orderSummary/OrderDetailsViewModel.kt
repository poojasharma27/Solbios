package com.solbios.ui.viewModel.home.orderSummary

import androidx.databinding.ObservableField
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solbios.network.ApiState
import com.solbios.ui.fragment.cart.orderDetails.OrderDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(val repository: OrderDetailsRepository, val stateHandle: SavedStateHandle) :ViewModel() {

    val id = stateHandle.get<String>("id")
    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }
    var progressVisibility = ObservableField(false)


    fun getOrderDetails(header:String?){
        viewModelScope.launch {
            repository.getOrderDetails(header,id).onStart {
                _apiState.value=ApiState.Loading
                progressVisibility.set(true)

            }.catch {e->
                _apiState.value=ApiState.Failure(e)
                progressVisibility.set(false)

            }.collect {
                _apiState.value=ApiState.Success(it)
                progressVisibility.set(false)

            }
        }
    }
}