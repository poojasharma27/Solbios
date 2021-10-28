package com.solbios.ui.viewModel.authviewmodel

import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solbios.model.SignUpRoot
import com.solbios.network.ApiState
import com.solbios.ui.fragment.signup.SignUpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.regex.Pattern
import androidx.navigation.findNavController
import com.solbios.other.Constants
import com.solbios.other.Constants.mobileNumber
import com.solbios.other.Constants.name
import com.solbios.other.Constants.signUp
import com.solbios.ui.fragment.signup.RegisterFragmentDirections
import org.json.JSONObject
import retrofit2.HttpException


@HiltViewModel
class SignUpViewModel @Inject constructor(var repository: SignUpRepository):ViewModel() {

    val signup= ObservableField<SignUpRoot>()
    var fullName:String?=null
    var email:String?=null
    var mobile:String?=null
    var password:String?=null
    var progressVisibility = ObservableField(false)
    var textVisibility = ObservableField(false)
    var  validation =MutableLiveData<String>()
     var errorThrow= MutableLiveData<String>()
    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }

  fun onSignUp(view: View) {
      if(TextUtils.isEmpty(fullName)){
        validation.value= name
      }
       else if (!isValidMobile(mobile)){
          validation.value= mobileNumber
      }
     else if(TextUtils.isEmpty(email) ){
         validation.value=Constants.email

       }else if(TextUtils.isEmpty(password) ){
           validation.value=Constants.password
       }
       else{
        getSignUp(view)
       }
  }

    fun getSignUp(view: View){
        viewModelScope.launch {
                repository.getSignUp(
                    fullName.toString(),
                    email.toString(),
                    mobile.toString(),
                    password.toString()
                ).onStart {

                    _apiState.value = ApiState.Loading
                    progressVisibility.set(true)


                }.catch { e ->
                    val error= (e as? HttpException)?.response()?.errorBody()?.string()
                    var obj = JSONObject(error)
                    var name= obj["message"]
                    _apiState.value = ApiState.Failure(e)
                    progressVisibility.set(false)
                    errorThrow.value=name.toString()


                }.collect {
                    progressVisibility.set(false)
                    if (it.code == 200) {
                        _apiState.value = ApiState.Success(it.data?.user_id)
                        val s: String = java.lang.String.valueOf(it.data?.user_id)
                         actionOnClick(s,view)


                    }

                     else {
                        Log.d("TAG", it.message.toString())
                    }
                }
            }

    }

    private fun actionOnClick(userId: String,view: View) {
        val action = RegisterFragmentDirections.actionRegisterFragmentToOtpFragment(userId  ,signUp )
        view.findNavController().navigate(action)
    }


    private fun isValidMail(email: String?): Boolean {
        val EMAIL_STRING = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
        return Pattern.compile(EMAIL_STRING).matcher(email).matches()
    }


    private fun isValidMobile(mobile:String?):Boolean{
       return mobile?.length==10 && !TextUtils.isEmpty(mobile)
    }
}