package com.solbios.di

import com.solbios.interfaces.ApiServiceIn
import com.solbios.ui.dialog.FilterRepository
import com.solbios.ui.fragment.cart.address.AddAddressRepository
import com.solbios.ui.fragment.cart.applyCoupon.ApplyCouponRepository
import com.solbios.ui.fragment.cart.CartRepository
import com.solbios.ui.fragment.cart.address.SelectAddressRepository
import com.solbios.ui.fragment.cart.orderDetails.OrderDetailsRepository
import com.solbios.ui.fragment.cart.orderSummary.OrderSummaryRepository
import com.solbios.ui.fragment.cart.payment.PaymentRepository
import com.solbios.ui.fragment.cart.userOrder.UserOrderDetailsRepository
import com.solbios.ui.fragment.forgot.ForgotRepository
import com.solbios.ui.fragment.home.home.HomeRepository
import com.solbios.ui.fragment.home.home.product.ProductListDescriptionRepository
import com.solbios.ui.fragment.home.home.product.ProductListRepository
import com.solbios.ui.fragment.home.search.SearchRepository
import com.solbios.ui.fragment.login.LoginRepository
import com.solbios.ui.fragment.signup.OtpRepository
import com.solbios.ui.fragment.signup.SignUpRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object RepositoryModule {

    @Provides
    fun provideSignUpRepo(apiService: ApiServiceIn) = SignUpRepository(apiService)
    @Provides
    fun provideOtpRepo(apiService: ApiServiceIn) = OtpRepository(apiService)
    @Provides
    fun provideLoginRepo(apiService: ApiServiceIn) = LoginRepository(apiService)
    @Provides
    fun provideForgotRepo(apiService: ApiServiceIn) = ForgotRepository(apiService)
    @Provides
    fun provideForgotOtpVerifyRepo(apiService: ApiServiceIn) = OtpRepository(apiService)
    @Provides
    fun provideUserResetRepo(apiService: ApiServiceIn) = OtpRepository(apiService)
    @Provides
    fun providesHomeRepo(apiService: ApiServiceIn)= HomeRepository(apiService)
    @Provides
    fun providesProductionRepo(apiService: ApiServiceIn)= ProductListRepository(apiService)
    @Provides
    fun providesFilterRepo(apiService: ApiServiceIn)=FilterRepository(apiService)
    @Provides
    fun providesProductDetailsRepo(apiService: ApiServiceIn)=ProductListDescriptionRepository(apiService)
    @Provides
    fun providesCartRepo(apiService: ApiServiceIn)=CartRepository(apiService)
    @Provides
    fun providesApplyCouponRepo(apiService: ApiServiceIn)= ApplyCouponRepository(apiService)
    @Provides
    fun providesSelectAddressRepo(apiService: ApiServiceIn)= SelectAddressRepository(apiService)
    @Provides
    fun providesAddAddressRepo(apiService: ApiServiceIn)= AddAddressRepository(apiService)
    @Provides
    fun providesOrderSummaryRepo(apiService: ApiServiceIn)= OrderSummaryRepository(apiService)
    @Provides
    fun providesOrderIdRepo(apiService: ApiServiceIn)= PaymentRepository(apiService)
    @Provides
    fun providesUserOrderDetailsRepo(apiService: ApiServiceIn)= UserOrderDetailsRepository(apiService)
     @Provides
    fun providesOrderDetailsRepo(apiService: ApiServiceIn)= OrderDetailsRepository(apiService)
    @Provides
    fun providesSearchRepo(apiService: ApiServiceIn)= SearchRepository(apiService)

}