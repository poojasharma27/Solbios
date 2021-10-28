package com.solbios.model.productList

data class CartProduct(
    val created_at: String,
    val format_created_at: String,
    val id: Int,
    val product_id: Int,
    val quantity: Int,
    val total_price: Any,
    val updated_at: String,
    val user_id: Int
)