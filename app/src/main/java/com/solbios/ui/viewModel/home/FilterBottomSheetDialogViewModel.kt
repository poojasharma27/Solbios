package com.solbios.ui.viewModel.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solbios.network.ApiState
import com.solbios.ui.dialog.FilterRepository
import com.solbios.ui.fragment.home.home.product.ProductListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterBottomSheetDialogViewModel @Inject constructor(private val filterRepository: FilterRepository , val productListRepository: ProductListRepository):ViewModel() {


    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)
     val _apiStateProduct= MutableStateFlow<ApiState>(ApiState.Empty)
       val category=MutableLiveData<String>()
       val brand=MutableLiveData<String>()
     val categoryId=MutableLiveData<Int>()
     val brandId=MutableLiveData<Int>()
    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }
    val apiStateProduct: StateFlow<ApiState> by lazy {
        _apiStateProduct
    }


    fun getFilter(){
        viewModelScope.launch {
            filterRepository.getFilter().onStart {
                _apiState.value=ApiState.Loading

            }.catch {e->
                _apiState.value=ApiState.Failure(e)


            }.collect {
                _apiState.value=ApiState.Success(it.data)

            }
        }
    }



    fun selectedList(){
       category.value="categorySelected"
    }

    fun brandSelectedList(){
        brand.value="brandSelected"
    }
}