package com.solbios.interfaces

import com.solbios.model.*
import com.solbios.model.addAddress.AddAddressRoot
import com.solbios.model.addtocart.AddToCartRoot
import com.solbios.model.cart.CartRoot
import com.solbios.model.cart.applycoupon.ApplyCouponRoot
import com.solbios.model.cart.applycoupon.CouponRoot
import com.solbios.model.cart.deleteCartItem.DeleteCartItem
import com.solbios.model.filter.FilterRoot
import com.solbios.model.home.PopularCategoriesRoot
import com.solbios.model.orderDetails.OrderDetailsRoot
import com.solbios.model.orderId.OrderIdRoot
import com.solbios.model.orderSummary.OrderSummaryRoot
import com.solbios.model.paymentCreateOrder.CreateOrderIdRoot
import com.solbios.model.productDetails.ProductDetailsRoot
import com.solbios.model.productList.ProductionList
import com.solbios.model.search.SearchRoot
import com.solbios.model.selectAddress.SelectAddressRoot
import com.solbios.model.selectAddress.removeList.DeleteAddressRoot
import com.solbios.model.userOrder.UserOrderRoot
import retrofit2.http.*

interface ApiServiceIn {

    @POST("register-user")
    suspend fun getSignUp(@Query("name") name: String, @Query("email") email : String, @Query("mobile_number") mobile_number : String, @Query("password") password : String): SignUpRoot

    @POST("otp-verify")
    suspend fun getOtp(@Query("user_id") userId:String?,@Query("otp_code") otpCode:String?) :OtpRoot

    @POST("user-login")
    suspend fun userLogin(@Query("mobile_number") mobileNumber:String?,@Query("password") password: String?): LoginRoot

    @GET("user-forgot-password")
    suspend fun userForgotPassword(@Query("mobile_number") mobileNumber:String?):ForgotRoot

    @POST("forgot-otp-verify")
    suspend fun forgotOtpVerify(@Query("user_id") userId:String?,@Query("otp_code") otpCode:String?) :ForgotOtpVerifyRoot

    @POST("resend-otp")
    suspend fun resendOtp(@Query("user_id") userId:String?) :ResendOtpRoot

    @PUT("user-reset-password")
    suspend fun userResetPassword(@Query("user_id") userId:String?,@Query("new_password") newPassword:String?) :UserResetRoot

    @GET("home-screen")
    suspend fun homeScreen(@Header ("Authorization") token:String?):PopularCategoriesRoot

    @GET("product-list")
    suspend fun productList(@Header ("Authorization") token:String?,@Query("category_id") categorieId:String?, @Query("brand_id") brandId:String?, @Query("sort")asc:String?, @Query("page") page:Int?):ProductionList

    @GET("filter-list")
    suspend fun filterList():FilterRoot

    @GET("product-detail")
    suspend fun productDetails(@Header ("Authorization") token:String?,@Query("id") id:Int?):ProductDetailsRoot

   @GET("get-cart-list")
    suspend fun cartDetails(@Header ("Authorization") token:String?): CartRoot

   @GET("coupon-list")
    suspend fun applyCoupon(@Header ("Authorization") token:String?): ApplyCouponRoot

    @POST("coupon-apply")
    suspend fun couponApply(@Header ("Authorization") token:String?,@Query("total_amount") totalAmount:String?,@Query("coupon_code") couponCode:String?): CouponRoot

    @GET("user-address-list")
    suspend fun userAddressList(@Header ("Authorization") token:String?): SelectAddressRoot

   @GET("my-orders")
    suspend fun userOrdersDetails(@Header ("Authorization") token:String?,@Query("page") page:Int?): UserOrderRoot

    @GET("user-address-detail")
    suspend fun userAddressDetail(@Header ("Authorization") token:String?,@Query("id") id:String?): AddAddressRoot

    @GET("online-detail")
    suspend fun orderId(@Header ("Authorization") token:String?,@Query("amount") amount:String?): OrderIdRoot

    @GET("global-search")
    suspend fun search(@Header ("Authorization") token:String?,@Query("keyword") keyword:String?): SearchRoot

    @GET("order-preview")
    suspend fun orderSummary(@Header ("Authorization") token:String?,@Query("address_id") id:Int?): OrderSummaryRoot

   @GET("my-order-detail")
    suspend fun orderDetails(@Header ("Authorization") token:String?,@Query("id") id:String?): OrderDetailsRoot

    @POST("add-to-cart")
    suspend fun addToCart(@Header ("Authorization") token:String?,@Query("product_id") productId:Int?,@Query("action") action:Int?):AddToCartRoot

    @POST("add-address")
    suspend fun addAddress(@Header ("Authorization") token:String?,@Query("address") address:String?,@Query("name") name:String?,@Query("state") state:String?,@Query("city") city:String?,@Query("pincode") pinCode:String?,@Query("address_type") addressType:Int?,@Query("contact_number") contact_number:String?,@Query("id")id:String?):AddAddressRoot

    @DELETE("remove-product")
    suspend fun deleteCartItem(@Header ("Authorization") token:String?,@Query("id") id:Int?):DeleteCartItem

   @DELETE("remove-address")
    suspend fun deleteAddress(@Header ("Authorization") token:String?,@Query("id") id:Int?):DeleteAddressRoot

     @POST("create_order")
     suspend fun createOrderId(@Header("Authorization") token:String?,@Query("amount") amount: String?,@Query("address_id") addressId:Int?,@Query("order_id") orderId:String?,@Query("payment_type") paymentType:Int?,@Query("transaction_id") transtionId:String?,@Query("status") status:Int?,@Query("reason") reason:String?):CreateOrderIdRoot
}