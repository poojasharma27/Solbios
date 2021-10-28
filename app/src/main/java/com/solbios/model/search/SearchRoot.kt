package com.solbios.model.search

data class SearchRoot(
    val code: Int,
    val `data`: List<Data>,
    val message: String,
    val next_page: Int,
    val total: Int
)