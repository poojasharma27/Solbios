package com.solbios.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchData(
  /*  val brand_id: Int,
    val cart_product_count: Int,
    val category_id: Int,
    val created_at: String,
    val description: String,
    val expiry_date: String,
    val format_created_at: String,
   val get_brand: GetBrand,
    val get_category: GetCategory,
    val group_id: Int,*/
    val id: Int,
    val image: String,
    val sub_title: String,
    val title: String,
   /* val pack_size: String,
    val price: Int,
    val sales_price: Int,
    val sku_code: String,
    val status: Int,
    val sub_category_id: Any,
    val thumbnail: String,
    val updated_at: String*/
){
    @PrimaryKey(autoGenerate = true)
    var searchId = 0
}