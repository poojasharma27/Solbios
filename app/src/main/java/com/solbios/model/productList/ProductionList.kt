package com.solbios.model.productList

data class ProductionList(
    val code: Int,
    val cart_total: Int,
    val `data`: List<Data>,
    val message: String,
    val next_page: Int,
    val total: Int
)