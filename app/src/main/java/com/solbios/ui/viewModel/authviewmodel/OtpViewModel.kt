package com.solbios.ui.viewModel.authviewmodel

import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.solbios.R
import com.solbios.network.ApiState
import com.solbios.other.Constants.Otp
import com.solbios.other.Constants.signUp
import com.solbios.ui.fragment.signup.OtpFragmentDirections
import com.solbios.ui.fragment.signup.OtpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject


@HiltViewModel
class OtpViewModel @Inject constructor( val repository: OtpRepository, val stateHandle: SavedStateHandle):ViewModel() {

 var otpCode:String?=null

    val data = stateHandle.get<String>("userId")
    val type=stateHandle.get<String>("type")
    var progressVisibility = ObservableField(false)
    var errorThrow=MutableLiveData<String>()
    var validation=MutableLiveData<String>()
    private val _apiState= MutableStateFlow<ApiState>(ApiState.Empty)
    val apiState: StateFlow<ApiState> by lazy {
        _apiState
    }
    /*--counter---*/
    lateinit var mcountdowntimer: CountDownTimer
    private val START_TIME_IN_MILLIS: Long = 10000
    private var mTimeLeftInMillis: Long = START_TIME_IN_MILLIS
    var mTimerRunning: Boolean? = null
     var timeLeftFormatted: String?=null
    var counter = ObservableField("")
    var resendClicked = false
    val time =MutableLiveData<String>()


    fun otpVerify(view: View){
      typeCheck(view)
    }

    private fun typeCheck(view: View) {
        if (!isValidMobile(otpCode)){
            validation.value=Otp

        }else {
            if (type == signUp) {
                otpSignUp(view)
            } else {
                otpForgotVerify(view)
            }
        }
    }

    private fun otpSignUp(view: View) {
        viewModelScope.launch {
            repository.getOtp(data,otpCode).onStart {
                _apiState.value = ApiState.Loading

                progressVisibility.set(true)

            }.catch {e->
                val error= (e as? HttpException)?.response()?.errorBody()?.string()
                var obj = JSONObject(error)
                var name= obj["message"]
                _apiState.value = ApiState.Failure(e)
                progressVisibility.set(false)
                errorThrow.value=name.toString()

            }.collect {
                _apiState.value = ApiState.Success(it.data)

                progressVisibility.set(false)
                 val action=OtpFragmentDirections.actionOtpFragmentToDashboardFragment("SignUp")
                 view.findNavController().navigate(action)

            }
        }
    }

    private fun otpForgotVerify(view: View) {
        viewModelScope.launch {
            repository.forgotOtpVerify(data,otpCode).onStart {
                _apiState.value = ApiState.Loading

                progressVisibility.set(true)

            }.catch {e->
                val error= (e as? HttpException)?.response()?.errorBody()?.string()
                var obj = JSONObject(error)
                var name= obj["message"]
                _apiState.value = ApiState.Failure(e)
                progressVisibility.set(false)
                errorThrow.value=name.toString()

            }.collect {
                _apiState.value = ApiState.Success(it.data)
                progressVisibility.set(false)
               val s: String = java.lang.String.valueOf(it.data?.user_id)
                actionOnClick(s,view)

            }
        }
    }


     fun resendOtp(view: View){
         resendClicked = true
         if (timeLeftFormatted.equals("00:00")&&resendClicked==true) {
             mTimeLeftInMillis = START_TIME_IN_MILLIS;
             updateCountDownText();
             timerCounter()
         }
         resendClicked=false
         viewModelScope.launch {
            repository.resendOtp(data).onStart {
                _apiState.value = ApiState.Loading

                progressVisibility.set(true)

            }.catch {e->
                val error= (e as? HttpException)?.response()?.errorBody()?.string()
                var obj = JSONObject(error)
                var name= obj["message"]
                _apiState.value = ApiState.Failure(e)
                progressVisibility.set(false)
                errorThrow.value=name.toString()

            }.collect {
                _apiState.value = ApiState.Success(it.data)
                progressVisibility.set(false)
                errorThrow.value=it.message
                val s: String = java.lang.String.valueOf(it.data?.user_id)

            }
        }

     }
    private fun actionOnClick(s: String, view: View) {
        val action = OtpFragmentDirections.actionOtpFragmentToForgotOtpVerifyFragment(s)
        view.findNavController().navigate(action)
    }
    private fun isValidMobile(otpCode:String?):Boolean{
        return otpCode?.length==4 && !TextUtils.isEmpty(otpCode)
    }


    fun timerCounter(){
        mcountdowntimer= object :CountDownTimer(mTimeLeftInMillis,1000){

            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                updateCountDownText()


            }

            override fun onFinish() {
                mTimerRunning = false
                counter.set(timeLeftFormatted)
                if (timeLeftFormatted=="00:00"){
                    time.value="00:00"
                }

            }

        }.start()
        mTimerRunning = true

    }

    private fun updateCountDownText() {
        val minutes = (mTimeLeftInMillis / 1000).toInt() / 60
        val seconds = (mTimeLeftInMillis / 1000).toInt() % 60
        timeLeftFormatted =
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        counter.set(timeLeftFormatted)

    }


}