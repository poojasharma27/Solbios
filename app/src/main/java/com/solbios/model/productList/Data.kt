package com.solbios.model.productList

import kotlin.math.roundToInt

data class Data(
    val average_rating: Any,
    val brand_id: Int,
    val category_id: Int,
    val created_at: String,
    val description: String,
    val expiry_date: String,
    val format_created_at: String,
    val get_brand: GetBrand,
    val get_category: GetCategory,
    val cart_product:CartProduct,
    val id: Int,
    val image: String,
    val pack_size: String,
    val price: Float,
val sales_price: Float,
    val sku_code: String,
    val status: Int,
    val sub_category_id: Any,
    val sub_title: String,
    val thumbnail: String,
    val title: String,
    val updated_at: String
) : Comparable<Data> {
    override fun compareTo(other: Data): Int {
       return (this.sales_price-other.sales_price).roundToInt()
    }
}