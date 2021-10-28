package com.solbios.ui.viewModel.home.address

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solbios.network.ApiState
import com.solbios.ui.fragment.cart.address.SelectAddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectAddressViewModel @Inject constructor(val repository: SelectAddressRepository)  :ViewModel() {

    var progressVisibility = ObservableField(false)
    var noDataItemVisibility = ObservableField(false)

    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }
    private val _apiStateDelete= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiStateDelete: StateFlow<ApiState> by lazy {
        _apiStateDelete
    }

    fun selectAddress(header:String?){
        viewModelScope.launch {
            repository.selectAddress(header).onStart {
                _apiState.value=ApiState.Loading
                progressVisibility.set(true)

            }.catch { e->
                _apiState.value=ApiState.Failure(e)
                progressVisibility.set(false)
            }.collect {

                if(it.data.size==0){
                    noDataItemVisibility.set(true)
                    progressVisibility.set(false)

                }
                else{

                    _apiState.value=ApiState.Success(it)
                    progressVisibility.set(false)
                }

            }
        }
    }

    fun deleteAddress(header:String?,id:Int){
        viewModelScope.launch {
            repository.deleteAddress(header,id).onStart {
                _apiStateDelete.value=ApiState.Loading
                progressVisibility.set(true)

            }.catch { e->
                _apiStateDelete.value=ApiState.Failure(e)
                progressVisibility.set(false)
            }.collect {
                    _apiStateDelete.value=ApiState.Success(it)
                    progressVisibility.set(false)
                }

            }
        }

}