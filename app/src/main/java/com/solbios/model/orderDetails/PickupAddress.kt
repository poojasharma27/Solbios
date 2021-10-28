package com.solbios.model.orderDetails

data class PickupAddress(
    val address: String,
    val address_type: Int,
    val city: String,
    val contact_number: Long,
    val created_at: String,
    val format_address_type: String,
    val format_created_at: String,
    val id: Int,
    val name: String,
    val pincode: Int,
    val state: String,
    val status: Int,
    val updated_at: String,
    val user_id: Int
)