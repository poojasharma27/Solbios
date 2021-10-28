package com.solbios.ui.fragment.signup

import com.solbios.interfaces.ApiServiceIn
import com.solbios.model.SignUpRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Dispatcher
import javax.inject.Inject

class SignUpRepository @Inject constructor(val apiServiceIn: ApiServiceIn){


    suspend fun getSignUp(name:String,email:String,mobileNumber:String,password:String)=flow<SignUpRoot>{
        emit(apiServiceIn.getSignUp(name,email,mobileNumber,password))
    }.flowOn(Dispatchers.IO)

}