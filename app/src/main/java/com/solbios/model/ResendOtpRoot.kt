package com.solbios.model

data class ResendOtpRoot(
    val code: Int,
    val `data`: ResendData,
    val message: String
)