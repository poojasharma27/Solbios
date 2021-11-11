package com.solbios.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.media.audiofx.BassBoost
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.firstapp.sharedPreference.SessionManagement
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.solbios.databinding.ActivityMainBinding
import com.solbios.databinding.ActivityNoInternetBinding
import com.solbios.other.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_no_internet.*
import org.json.JSONObject


@AndroidEntryPoint
class AuthActivity : AppCompatActivity(), PaymentResultWithDataListener {
  var from:String?=null

    private var binding: ActivityMainBinding?=null
    private var noBinding: ActivityNoInternetBinding?=null
  lateinit var navController:NavController
    var sessionManagement: SessionManagement?=null
    /*private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 60
    }

       private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()

                Toast.makeText(
                    this@AuthActivity,
                    "Got Location: " + location.toString(),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            else{
                Log.e("Location","null")
            }
        }
    }

*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding?.root)
            val intent = intent
            from = intent.getStringExtra("from")
            createNavHost()
            sessionManagement = this?.let { SessionManagement(it) }
            Checkout.preload(applicationContext)
            /* fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        checkLocationPermission()*/
    /*else{
            noBinding= ActivityNoInternetBinding.inflate(layoutInflater)
            setContentView(noBinding?.root)
            Toast.makeText(this,"you are offline.Please check your Internet connection ",Toast.LENGTH_LONG).show()
            tryAgain.setOnClickListener {
               val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                 val netInfo = cm.getActiveNetworkInfo()
            }

        }*/
    }

/*
    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.removeLocationUpdates(locationCallback)
        }
    }*/

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
        val obj = JSONObject(p1)
        val errorObj= obj["error"].toString()
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


  /*  private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        } else {
            checkBackgroundLocation()
        }
    }

    private fun checkBackgroundLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestBackgroundLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper())

                     fusedLocationProvider?.lastLocation
                            ?.addOnSuccessListener { location : Location? ->
                                addpathPoints(location)
                                Log.e("Location","${location?.longitude}")
                                // Got last known location. In some rare situations this can be null.
                            }
                        // Now check background location
                        checkBackgroundLocation()
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()

                    // Check if we are in a state where the user has denied the permission and
                    // selected Don't ask again
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", this.packageName, null),
                            ),
                        )
                    }
                }
                return
            }
            MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                  fusedLocationProvider?.lastLocation
                            ?.addOnSuccessListener { location : Location? ->
                                addpathPoints(location)
                            }

                        Toast.makeText(
                            this,
                            "Granted Background Location Permission",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return

            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
    }

     fun addpathPoints(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            Log.e("Location","${pos}")
        }
    }*/



}