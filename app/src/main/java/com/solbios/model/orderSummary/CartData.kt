package com.solbios.model.orderSummary

data class CartData(
    val created_at: String,
    val format_created_at: String,
    val get_product: GetProduct,
    val id: Int,
    val product_id: Int,
    val quantity: Int,
    val total_price: Any,
    val total_product_price: Int,
    val total_real_price: Int,
    val updated_at: String,
    val user_id: Int
)