package com.solbios.ui.fragment.signup

import com.solbios.interfaces.ApiServiceIn
import com.solbios.model.ForgotOtpVerifyRoot
import com.solbios.model.OtpRoot
import com.solbios.model.ResendOtpRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class OtpRepository @Inject constructor( val apiServiceIn: ApiServiceIn) {

  suspend fun  getOtp(userId:String?,otpCode:String?)= flow<OtpRoot> {
      emit(apiServiceIn.getOtp(userId,otpCode))
       }.flowOn(Dispatchers.IO)

    suspend fun  forgotOtpVerify(userId:String?,otpCode:String?)= flow<ForgotOtpVerifyRoot> {
        emit(apiServiceIn.forgotOtpVerify(userId,otpCode))
    }.flowOn(Dispatchers.IO)

    suspend fun  resendOtp(userId:String?)= flow<ResendOtpRoot> {
        emit(apiServiceIn.resendOtp(userId))
    }.flowOn(Dispatchers.IO)

}