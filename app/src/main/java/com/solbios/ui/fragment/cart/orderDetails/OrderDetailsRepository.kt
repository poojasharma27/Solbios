package com.solbios.ui.fragment.cart.orderDetails

import com.solbios.interfaces.ApiServiceIn
import com.solbios.model.orderDetails.OrderDetailsRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Dispatcher
import javax.inject.Inject

class OrderDetailsRepository @Inject  constructor( val apiServiceIn: ApiServiceIn) {

    suspend fun getOrderDetails(header:String?,id:String?)= flow<OrderDetailsRoot> {
        emit(apiServiceIn.orderDetails(header,id))
    }.flowOn(Dispatchers.IO)
}