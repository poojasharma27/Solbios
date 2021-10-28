package com.solbios.model.cart.applycoupon

data class ApplyCouponData(
    val coupon_code: String,
    val coupon_title: String,
    val created_at: String,
    val description: String,
    val expiry_date: String,
    val format_created_at: String,
    val format_expiry_date: String,
    val id: Int,
    val image: String,
    val max_percentage: Any,
    val maximum_discount: Int,
    val minimum_price: Int,
    val status: Int,
    val total_count: Int,
    val type: Int,
    val updated_at: String
)