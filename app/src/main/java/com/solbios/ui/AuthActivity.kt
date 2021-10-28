package com.solbios.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.firstapp.sharedPreference.SessionManagement
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.solbios.databinding.ActivityMainBinding
import com.solbios.other.Constants
import com.solbios.other._failure
import com.solbios.other._success
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class AuthActivity : AppCompatActivity(), PaymentResultWithDataListener {
  var from:String?=null

    private var binding: ActivityMainBinding?=null
  lateinit var navController:NavController
    var sessionManagement: SessionManagement?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val intent = intent
        from=intent.getStringExtra("from")
        createNavHost()
        sessionManagement= this?.let { SessionManagement(it) }
        Checkout.preload(applicationContext)

    }



    private fun createNavHost() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(com.solbios.R.id.nav_host_fragment) as NavHostFragment
         navController = navHostFragment.navController
        val bundle = Bundle()
        bundle.putString("from",from)
        navController.setGraph(navController.graph,bundle)


    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()

    }

     fun startPayment(orderId:String) {
        val checkout = Checkout()
         checkout.setKeyID("rzp_test_SDcX6BXYRgEMnl");
        try {
            val options = JSONObject()
            options.put("name", sessionManagement?.getUserName())
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("theme.color", "#3399cc")
           options.put("order_id", orderId)
            options.put("currency", "INR")
           /* var total: Double = orderId.toDouble()
            total = total * 100
            options.put("amount", total)*/
            options.put("prefill.email", sessionManagement?.getUserEmail())
            options.put("prefill.contact", sessionManagement?.getUserMobileNumber())
           val retryObj = JSONObject()
            retryObj.put("enabled", false)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)
            checkout.open(this, options)
        } catch (e: Exception) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e)
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        val bundle = Bundle()
        bundle.putString(Constants.orderId, p1?.orderId)
        bundle.putString(Constants.paymentId, p1?.paymentId)
        _success.value=bundle
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        var obj = JSONObject(p1)
        var errorObj= obj["error"].toString()
        var codeObj=JSONObject(errorObj)
        var reasonString=codeObj["reason"].toString()
        var codeValue=codeObj["metadata"].toString()
        var metaDataObj=JSONObject(codeValue)
        var paymentId=metaDataObj["payment_id"].toString()
        var orderId=metaDataObj["order_id"].toString()
        val bundle = Bundle()
        bundle.putString(Constants.orderId, orderId)
        bundle.putString(Constants.paymentId, paymentId)
        bundle.putString(Constants.reason, reasonString)
        _failure.value=bundle

    }



}