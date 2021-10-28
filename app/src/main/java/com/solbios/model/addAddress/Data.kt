package com.solbios.model.addAddress

data class Data(
    val address: String,
    val address_type: Int,
    val city: String,
    val contact_number: String,
    val created_at: String,
    val format_address_type: String,
    val format_created_at: String,
    val id: Int,
    val name: String,
    val pincode: String,
    val state: String,
    val status: Int,
    val updated_at: String,
    val user_id: Int
)