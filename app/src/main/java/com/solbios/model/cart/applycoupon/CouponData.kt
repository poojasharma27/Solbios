package com.solbios.model.cart.applycoupon

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CouponData(
    var discount_amount: Double,
    var total_discounted_amount: Double,
    var coupon_id: Int,
    var coupon_title: String,
    var coupon_code: String,
    val minimum_price: Double
):Parcelable