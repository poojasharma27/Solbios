package com.solbios.mapper

import com.solbios.db.entities.CouponDetails
import com.solbios.db.entities.SearchData
import com.solbios.model.cart.applycoupon.CouponData
import com.solbios.model.search.Data


fun Data.toSearchDataEntity() : SearchData {
     val searchData= SearchData(
         /*this . brand_id,
         this . cart_product_count,
         this . category_id,
         this . created_at,
         this . description,
         this . expiry_date,
         this . format_created_at,*/
         this.id,
        this.image,
       this .title,
         this .sub_title

     )
    return searchData
}

fun CouponData.toCouponData():CouponDetails{
   val couponData=CouponDetails(
       this .discount_amount,
    this. total_discounted_amount,
     this. coupon_id,
     this. coupon_title,
     this. coupon_code,
       this. minimum_price
   )
    return couponData
}
