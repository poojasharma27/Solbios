package com.solbios.ui.viewModel.authviewmodel

import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.solbios.R
import com.solbios.network.ApiState
import com.solbios.other.Constants
import com.solbios.other.Constants.mobileNumber
import com.solbios.other.Constants.password
import com.solbios.other.Constants.signUp
import com.solbios.ui.fragment.login.LoginFragmentDirections
import com.solbios.ui.fragment.login.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val loginRepository: LoginRepository) :ViewModel(){

  var mobile:String?=null
    var password:String?=null
    var progressVisibility = ObservableField(false)
    var validation = MutableLiveData<String>()
    val errorThrow=MutableLiveData<String>()

    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)

    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }

fun userLogin(view: View){
    if (!isValidMobile(mobile)){
        validation.value=mobileNumber
    }
    else if(TextUtils.isEmpty(password)) {
        validation.value= Constants.password

    }
   else{
        login(view)
    }

}

    private fun login(view: View) {
        viewModelScope.launch {
            loginRepository.login(mobile,password).onStart {
                _apiState.value=ApiState.Loading
                progressVisibility.set(true)
            }.catch {e->

                progressVisibility.set(false)

                val error= (e as? HttpException)?.response()?.errorBody()?.string()
                if (error!=null) {
                    var obj = JSONObject(error)
                    var name = obj["message"]
                    _apiState.value = ApiState.Failure(e)
                    errorThrow.value = name.toString()
                }else{
                    errorThrow.value="You are offline. Please check your internet connection"
                    progressVisibility.set(false)

                }

            }.collect {
                progressVisibility.set(false)


                if (it.code==200) {
                    _apiState.value = ApiState.Success(it.data)
                    Navigation.findNavController(view)
                        .navigate(R.id.action_loginFragment_to_dashboardFragment)
                }else if(it.code==201){
                    val userId: String = java.lang.String.valueOf(it.data?.user_id)
                    val action=LoginFragmentDirections.actionLoginFragmentToOtpFragment(userId,signUp)
                    view.findNavController().navigate(action)
                }
            }
        }

    }


    fun isValidPhone(phone: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(phone)) {

            false
        } else {
            Patterns.PHONE.matcher(phone).matches()
        }
    }

    private fun isValidPassword(password:String):Boolean{
        return TextUtils.isEmpty(password)
    }

    private fun isValidMobile(mobile:String?):Boolean{
        return mobile?.length==10 && !TextUtils.isEmpty(mobile)
    }
}