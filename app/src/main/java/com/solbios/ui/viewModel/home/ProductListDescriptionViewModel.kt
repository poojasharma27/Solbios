package com.solbios.ui.viewModel.home

import androidx.databinding.ObservableField
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solbios.network.ApiState
import com.solbios.ui.fragment.home.home.product.ProductListDescriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductListDescriptionViewModel @Inject constructor(val repository: ProductListDescriptionRepository) :ViewModel() {

    //val data = stateHandle.get<Int>("id")
    var progressVisibility = ObservableField(false)
    var screenVisibility = ObservableField(false)
    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }
    private val _apiStateAddtoCart= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiStateAddToCart: StateFlow<ApiState> by lazy {
        _apiStateAddtoCart
    }


    fun getDetails(header:String?,id:Int?){
        viewModelScope.launch {
            repository.getDetails(header,id).onStart {
                _apiState.value=ApiState.Loading
                progressVisibility.set(true)

            }.catch {e->
               screenVisibility.set(true)
                _apiState.value=ApiState.Failure(e)
                progressVisibility.set(false)

            }.collect {
                screenVisibility.set(true)
                _apiState.value=ApiState.Success(it.data)
                progressVisibility.set(false)

            }
        }


    }

    fun getAddToCart(header:String?,productId:Int?,action:Int?){
        viewModelScope.launch {
            repository.getAddToCart(header,productId,action).onStart {
                _apiStateAddtoCart.value = ApiState.Loading
            }.catch {e->
                _apiStateAddtoCart.value = ApiState.Failure (e)
            }.collect {
                _apiStateAddtoCart.value=ApiState.Success(it)

            }
        }
    }

}