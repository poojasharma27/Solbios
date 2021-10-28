package com.solbios.ui.fragment.cart.payment

import com.solbios.interfaces.ApiServiceIn
import com.solbios.model.orderId.OrderIdRoot
import com.solbios.model.paymentCreateOrder.CreateOrderIdRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PaymentRepository  @Inject constructor( val  apiServiceIn: ApiServiceIn) {

    suspend fun getOrderId(header:String?,amount:String?)= flow<OrderIdRoot> {
        emit(apiServiceIn.orderId(header,amount))

    }.flowOn(Dispatchers.IO)

    suspend fun createOrderId(header:String?,amount:String?,addressId:Int?,orderId:String?,paymentType:Int?,transationId:String?,status:Int?,reason:String?)= flow<CreateOrderIdRoot> {
        emit(apiServiceIn.createOrderId(header,amount,addressId,orderId,paymentType,transationId,status,reason))

    }.flowOn(Dispatchers.IO)
}