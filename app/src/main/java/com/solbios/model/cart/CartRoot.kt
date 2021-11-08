package com.solbios.model.cart

data class CartRoot(
    val code: Int,
    val `data`: List<Data>,
    val message: String,
    val total :Int,
    val tax_amount :Int

)