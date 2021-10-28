package com.solbios.model.userOrder

data class UserOrderRoot(
    val code: Int,
    val `data`: List<Data>,
    val message: String,
    val next_page: Int,
    val total: Int
)