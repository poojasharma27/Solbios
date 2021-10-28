package com.solbios.model.selectAddress

data class SelectAddressRoot(
    val code: Int,
    val `data`: List<Data>,
    val message: String,
    val next_page: Int,
    val total: Int,
    val total_final_price: Int
)