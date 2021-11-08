package com.solbios.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchEntityRoot(
    val code: Int,
    val `data`: List<SearchData>,
    val message: String,
    val next_page: Int,
    val total: Int
){
    @PrimaryKey(autoGenerate = true)
    var searchId = 0
}