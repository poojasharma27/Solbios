package com.solbios.ui.fragment.cart.orderSummary

import com.solbios.interfaces.ApiServiceIn
import com.solbios.model.orderSummary.OrderSummaryRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class OrderSummaryRepository @Inject constructor(val apiServiceIn: ApiServiceIn){

    suspend fun orderSummary(header: String?,id: Int?)= flow<OrderSummaryRoot> {
        emit(apiServiceIn.orderSummary(header,id))

    }.flowOn(Dispatchers.IO)

}