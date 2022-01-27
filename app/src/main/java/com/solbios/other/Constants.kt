package com.solbios.other

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object Constants {
    const val mobileNumber="mobileNumber"
    const val password="Password"
    const val ConfirmPassword="ConfirmPassword"
    const val name="Name"
    const val Enter_name="Please enter your valid name"
    const val Enter_address="Please Enter your locality"
    const val Enter_mobile_number="Please enter 10 digit mobile number"
    const val Enter_email_Id="Please enter your valid email"
    const val Otp="otpCode"
    const val email="email"
    const val signUp="SignUp"
    const val Enter_password="Please enter a password"
    const val Enter_confirmPassword="Please enter a confirm password"
    const val Enter_otp="Please enter a otpCode"
    const val selectAddress="SelectAddress"
    const val addAddress="AddAddress"
    const val pinCode="PinCode"
    const val Enter_pinCode="Please enter your pin Code"
    const val Enter_city="Please enter your City"
    const val Enter_state="Please enter your State"

    const val city="City"
    const val state="State"
    const val home="Home"
    const val orderSummary="OrderSummary"
    const val myOrder="My Orders"
    const val orderId="orderId"
    const val paymentId="paymentId"
    const val paymentDetails="PaymentDetails"
    const val reason="Reason"
    const val applyCoupon="Apply Coupon"
    const val cart="Cart"
    const val noInternet="You are offline. Please check your internet connection"
}


var _success  = MutableLiveData<Bundle>()
var Success : LiveData<Bundle>  = _success
var _failure  = MutableLiveData<Bundle>()
var Failure : LiveData<Bundle>  = _failure

var _address  = MutableLiveData<String>()
var _Address : LiveData<String>  = _address