package com.solbios.model.cart.applycoupon

data class CouponRoot(
    val code: Int,
    val `data`: CouponData,
    val message: String
)