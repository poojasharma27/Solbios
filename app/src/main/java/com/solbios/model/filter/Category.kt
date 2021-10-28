package com.solbios.model.filter

data class Category(
    val format_created_at: String,
    val id: Int,
    val image: String,
    val title: String,
    var selected: Boolean
)