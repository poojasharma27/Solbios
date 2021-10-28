package com.solbios.ui.fragment.cart.applyCoupon

import com.solbios.interfaces.ApiServiceIn
import com.solbios.model.cart.applycoupon.ApplyCouponRoot
import com.solbios.model.cart.applycoupon.CouponRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Dispatcher
import javax.inject.Inject

class ApplyCouponRepository @Inject constructor(val apiServiceIn: ApiServiceIn) {

    suspend fun getApplyCoupon(header:String)= flow<ApplyCouponRoot> {
        emit(apiServiceIn.applyCoupon(header))

    }.flowOn(Dispatchers.IO)

    suspend fun getCoupon(header:String?,totalAmount:String?,couponCode:String?)= flow<CouponRoot> {
        emit(apiServiceIn.couponApply(header,totalAmount,couponCode))

    }.flowOn(Dispatchers.IO)
}