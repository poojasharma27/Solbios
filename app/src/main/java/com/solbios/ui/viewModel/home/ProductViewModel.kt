package com.solbios.ui.viewModel.home

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solbios.network.ApiState
import com.solbios.ui.fragment.home.home.product.ProductListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(val productListRepository: ProductListRepository, val stateHandle: SavedStateHandle) : ViewModel(){

    var data = stateHandle.get<String>("id")
    var brandId = stateHandle.get<String>("brandId")
    var asc:String?=" "
     var noProduct=ObservableField(false)
    var progressVisibility = ObservableField(false)
    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }
    private val _apiStateFilter= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiStateFilter: StateFlow<ApiState> by lazy {
        _apiStateFilter
    }
    private val _apiStateAddtoCart= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiStateAddToCart: StateFlow<ApiState> by lazy {
        _apiStateAddtoCart
    }
    fun getProductList(header: String?,page:Int?){
        viewModelScope.launch {
            productListRepository.getProductionList(header,data,brandId,asc,page).onStart {
                _apiState.value = ApiState.Loading

                progressVisibility.set(true)
            }.catch {e->
                _apiState.value = ApiState.Failure(e)

                progressVisibility.set(false)
            }.collect {
                Log.e("TAG", "getProductList: $data", )
                _apiState.value = ApiState.Success(it)

                progressVisibility.set(false)
                if (it.total==0){
                    noProduct.set(true)
                }
                else{
                    noProduct.set(false)
                }
            }
        }
    }

    fun getAddToCart(header:String?,productId:Int?,action:Int?){
        viewModelScope.launch {
            productListRepository.getAddToCart(header,productId,action).onStart {
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

    fun getFilter(){
        viewModelScope.launch {
            productListRepository.getFilter().onStart {
                _apiStateFilter.value=ApiState.Loading

            }.catch {e->
                _apiStateFilter.value=ApiState.Failure(e)


            }.collect {
                _apiStateFilter.value=ApiState.Success(it.data)

            }
        }
    }

}