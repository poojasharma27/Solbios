package com.solbios.model.cart

data class Data(
    val created_at: String,
    val format_created_at: String,
    val get_product: GetProduct,
    val id: Int,
    val product_id: Int,
    var quantity: Int,
    val total_price: String,
    val updated_at: String,
    val user_id: Int,
    var total_product_price :Int
)