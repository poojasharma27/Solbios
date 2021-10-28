package com.solbios.model.paymentCreateOrder

data class Data(
    val address_id: String,
    val created_at: String,
    val format_created_at: String,
    val id: Int,
    val order_id: String,
    val payment_type: String,
    val status: Int,
    val total_amount: String,
    val transaction_id: String,
    val updated_at: String,
    val user_id: Int
)