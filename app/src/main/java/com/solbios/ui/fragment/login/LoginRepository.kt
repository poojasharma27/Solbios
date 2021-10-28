package com.solbios.ui.fragment.login

import com.solbios.interfaces.ApiServiceIn
import com.solbios.model.LoginRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LoginRepository @Inject constructor( val apiServiceIn: ApiServiceIn) {

    suspend fun login(mobile:String?,password:String?) = flow<LoginRoot>{
        emit(apiServiceIn.userLogin(mobile,password))
    }.flowOn(Dispatchers.IO)
}