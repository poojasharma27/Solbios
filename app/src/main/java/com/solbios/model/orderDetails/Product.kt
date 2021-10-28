package com.solbios.model.orderDetails

data class Product(
    val created_at: String,
    val format_created_at: String,
    val id: Int,
    val order_id: Int,
    val product_detail: ProductDetail,
    val product_id: Int,
    val quantity: Int,
    val updated_at: String
)