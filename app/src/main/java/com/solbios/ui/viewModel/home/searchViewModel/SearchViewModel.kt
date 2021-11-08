package com.solbios.ui.viewModel.home.searchViewModel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solbios.network.ApiState
import com.solbios.ui.fragment.home.search.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.internal.http2.Header
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor( val searchRepository: SearchRepository):ViewModel() {

    var noSearch= ObservableField(false)

    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }

    fun getSearch(header: String?,keyword:String?){
        viewModelScope.launch {
            searchRepository.getSearch(header,keyword).onStart {
          _apiState.value=ApiState.Loading
            }.catch {e->
                _apiState.value=ApiState.Failure(e)

            }.collect {
                _apiState.value=ApiState.Success(it)
                if (it.total==0 && it.trending_product.isEmpty()){
                  noSearch.set(true)

                }else{
                    noSearch.set(false)
                }

            }
        }
    }
}