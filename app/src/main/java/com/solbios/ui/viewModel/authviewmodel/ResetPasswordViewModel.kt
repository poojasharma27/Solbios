package com.solbios.ui.viewModel.authviewmodel

import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.solbios.R
import com.solbios.network.ApiState
import com.solbios.other.Constants
import com.solbios.other.Constants.ConfirmPassword
import com.solbios.ui.fragment.forgot.ForgotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject


@HiltViewModel
class ResetPasswordViewModel @Inject constructor( val forgotRepository: ForgotRepository,val stateHandle: SavedStateHandle)  :ViewModel() {

    var newPassword:String?=null
    var confirmPassword:String?=null
    val errorThrow= MutableLiveData<String>()

    val data =stateHandle.get<String>("userId")

    var progressVisibility = ObservableField(false)
    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)
    var textVisibility = ObservableField(false)
    val validation= MutableLiveData<String>()
    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }

    fun userSubmit(view: View){
        if (TextUtils.isEmpty(newPassword)){
            validation.value=Constants.password
        }else if (TextUtils.isEmpty(confirmPassword)){
            validation.value=ConfirmPassword
        }
        else {
            resetPassword(view)
        }

    }

    private fun resetPassword(view: View) {
        if (newPassword==confirmPassword) {
            viewModelScope.launch {
                forgotRepository.userReset(data, newPassword).onStart {
                    _apiState.value=ApiState.Loading
                    progressVisibility.set(true)

                }.catch {e->
                    val error= (e as? HttpException)?.response()?.errorBody()?.string()
                    if (error!=null) {
                        var obj = JSONObject(error)
                        var name = obj["message"]
                        _apiState.value = ApiState.Failure(e)
                        errorThrow.value = name.toString()
                    }  else{
                    errorThrow.value = "You are offline. Please check your internet connection"
                        progressVisibility.set(false)

                    }
                }.collect {
                    _apiState.value=ApiState.Success(it.message)
                    progressVisibility.set(false)
                    Navigation.findNavController(view).navigate(R.id.action_forgotOtpVerifyFragment_to_loginFragment)

                }
            }
        }else{
            textVisibility.set(true)
        }
    }

    private fun isValidPassword(password:String):Boolean{
        return TextUtils.isEmpty(password)
    }

}