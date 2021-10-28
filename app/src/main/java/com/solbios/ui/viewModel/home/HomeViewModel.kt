package com.solbios.ui.viewModel.home

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.solbios.R
import com.solbios.network.ApiState
import com.solbios.ui.fragment.home.home.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(val repository: HomeRepository):ViewModel() {

    var progressVisibility = ObservableField(false)

    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)

    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }

    fun getHomeDetails(header:String?){
        viewModelScope.launch {
            repository.homeDetails(header).onStart {
                progressVisibility.set(true)

                _apiState.value=ApiState.Loading

            }.catch { e->
                progressVisibility.set(false)

                _apiState.value=ApiState.Failure(e)

            }.collect {
                progressVisibility.set(false)

                _apiState.value=ApiState.Success(it)


            }
        }
    }

    fun searchOnClick(view: View){
        Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_searchFragment)

    }

}