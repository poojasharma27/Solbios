package com.solbios.model

data class LoginRoot(
    val code: Int?,
    val `data`: LoginData?,
    val message: String?
)