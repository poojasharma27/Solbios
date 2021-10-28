package com.solbios.model.orderSummary

data class OrderSummaryRoot(
    val code: Int,
    val `data`: Data,
    val message: String,
    val total: Int,
    val totalFinalPrice: Int,
    val totalRealPrice: Int
)