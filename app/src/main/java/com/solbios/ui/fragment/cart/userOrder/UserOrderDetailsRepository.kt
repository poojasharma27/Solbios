package com.solbios.ui.fragment.cart.userOrder

import com.solbios.interfaces.ApiServiceIn
import com.solbios.model.userOrder.UserOrderRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserOrderDetailsRepository @Inject constructor( val apiServiceIn: ApiServiceIn) {

    suspend fun getUserDetails(header:String?,page:Int?)= flow<UserOrderRoot> {
        emit(apiServiceIn.userOrdersDetails(header,page))

    }.flowOn(Dispatchers.IO)


}