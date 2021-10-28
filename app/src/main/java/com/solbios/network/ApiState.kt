package com.solbios.network

sealed class ApiState{

    object Empty :ApiState()
    object Loading :ApiState()


    class Success<T>(var data : T): ApiState()
    class Failure(var e : Throwable) : ApiState()

}
