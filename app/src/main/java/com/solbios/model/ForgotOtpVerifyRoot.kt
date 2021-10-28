package com.solbios.model

data class ForgotOtpVerifyRoot(
    val code: Int?,
    val `data`: ForgotOtpVerifyData?,
    val message: String?
)