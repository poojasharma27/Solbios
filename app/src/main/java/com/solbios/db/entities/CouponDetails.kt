package com.solbios.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CouponDetails(
    var discount_amount: Double,
    var total_discounted_amount: Double,
    var coupon_id: Int,
    var coupon_title: String,
    var coupon_code: String,
    val minimum_price: Double
){
    @PrimaryKey(autoGenerate = true)
    var couponID = 0
}
