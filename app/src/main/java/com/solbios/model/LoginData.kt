package com.solbios.model

data class LoginData(
    val created_at: String?,
    val email: String?,
    val id: Int?,
    val user_id: Int?,
    val is_mobile_verify: Int?,
    val mobile_number: String?,
    val name: String?,
    val status: Int?,
    val token: String?,
    val updated_at: String?
)