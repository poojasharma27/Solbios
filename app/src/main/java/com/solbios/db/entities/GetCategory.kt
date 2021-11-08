package com.solbios.db.entities

import androidx.room.Entity

@Entity
data class GetCategory(
    val format_created_at: String,
    val id: Int,
    val title: String
)