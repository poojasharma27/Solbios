package com.solbios.model.productList

data class ProductionList(
    val code: Int,
    val cart_total: Int,
    val cart_total_amount: Int,
    val `data`: List<Data>,
    val message: String,
    val next_page: Int,
    val total: Int
)