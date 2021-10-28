package com.solbios.model

data class UserResetRoot(
    val code: Int,
    val `data`: List<Any>,
    val message: String
)