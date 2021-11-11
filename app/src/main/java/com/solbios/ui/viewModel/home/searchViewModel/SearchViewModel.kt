package com.solbios.ui.viewModel.home.searchViewModel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solbios.network.ApiState
import com.solbios.ui.fragment.home.search.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor( private val searchRepository: SearchRepository):ViewModel() {

    var noSearch= ObservableField(false)
    var progressVisibility = ObservableField(false)

    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }

    fun getSearch(header: String?,keyword:String?){
        viewModelScope.launch {
            searchRepository.getSearch(header,keyword).onStart {
          _apiState.value=ApiState.Loading
                progressVisibility.set(true)
            }.catch {e->
                _apiState.value=ApiState.Failure(e)
                progressVisibility.set(false)


            }.collect {
                _apiState.value=ApiState.Success(it)
                progressVisibility.set(false)

                if (it.total==0 && it.trending_product.isEmpty()){
                  noSearch.set(true)

                }else{
                    noSearch.set(false)
                }

            }
        }
    }
}