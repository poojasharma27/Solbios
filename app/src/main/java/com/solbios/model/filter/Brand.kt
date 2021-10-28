package com.solbios.model.filter

data class Brand(
    val format_created_at: String,
    val id: Int,
    val image: String,
    val title: String,
    var selected: Boolean,
    var unSelected:Boolean
)