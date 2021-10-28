package com.solbios.model.cart.applycoupon

data class ApplyCouponRoot(
    val code: Int,
    val `data`: List<ApplyCouponData>,
    val message: String,
    val next_page: Int,
    val total: Int
)