package com.solbios.model.cart

data class GetProduct(
    val average_rating: Any,
    val brand_id: Int,
    val category_id: Int,
    val created_at: String,
    val description: String,
    val expiry_date: String,
    val format_created_at: String,
    val group_id: Int,
    val id: Int,
    val image: String,
    val pack_size: String,
    val price: Int,
    val sales_price: Int,
    val sku_code: String,
    val status: Int,
    val sub_category_id: Any,
    val sub_title: String,
    val thumbnail: String,
    val title: String,
    val updated_at: String
)