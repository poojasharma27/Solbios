package com.solbios.model.orderDetails

data class Data(
    val address_id: Int,
    val created_at: String,
    val current_status: CurrentStatus,
    val pickup_address: PickupAddress,
    val format_created_at: String,
    val id: Int,
    val order_id: String,
    val order_status: Int,
    val payment_type: Int,
    val products: List<Product>,
    val status: Int,
    val total_amount: Int,
    val transaction_id: String,
    val updated_at: String,
    val user_id: Int
)