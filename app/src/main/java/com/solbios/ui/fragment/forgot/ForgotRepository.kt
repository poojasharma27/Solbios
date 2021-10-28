package com.solbios.ui.fragment.forgot

import com.solbios.interfaces.ApiServiceIn
import com.solbios.model.ForgotOtpVerifyRoot
import com.solbios.model.ForgotRoot
import com.solbios.model.OtpRoot
import com.solbios.model.UserResetRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ForgotRepository @Inject constructor(val apiServiceIn: ApiServiceIn){

    suspend fun forgot(mobile:String?)= flow<ForgotRoot> {
        emit(apiServiceIn.userForgotPassword(mobile))
    }.flowOn(Dispatchers.IO)


    suspend fun userReset(userId:String?,newPassword:String?)=flow<UserResetRoot>{
        emit(apiServiceIn.userResetPassword(userId,newPassword))
    }.flowOn(Dispatchers.IO)

}