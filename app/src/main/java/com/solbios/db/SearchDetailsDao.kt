package com.solbios.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.solbios.db.entities.CouponDetails
import com.solbios.db.entities.SearchData
import com.solbios.db.entities.SearchEntityRoot
import com.solbios.model.cart.applycoupon.CouponData

@Dao
interface SearchDetailsDao {

    @Insert
    suspend fun addDetails(searchData: List<SearchData>)
    @Insert
    suspend fun searchAddDetails(searchData: SearchData)
    @Insert
    suspend fun  couponAddDetails(couponData: CouponDetails)

    @Query("SELECT * FROM searchdata")
   suspend   fun getAllRecentSearch(): List<SearchData?>?

   @Query("SELECT * FROM coupondetails")
   suspend   fun getCoupon(): List<CouponDetails?>?

  @Query("SELECT * FROM coupondetails")
   suspend   fun  getCouponDetails(): CouponDetails?

   @Query("DELETE FROM searchdata")
     suspend  fun deleteAll()

     @Query("DELETE FROM coupondetails")
     suspend  fun deleteAllCoupon()

}