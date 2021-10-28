package com.solbios.model.orderSummary

data class Data(
    val cart_data: List<CartData>,
    val delivery_address: DeliveryAddress,
    val order_id: String

)