package com.solbios.ui.fragment.home.home

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.adapter.HomeBannerAdapter
import com.firstapp.sharedPreference.SessionManagement
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.solbios.R
import com.solbios.databinding.ActivityNoInternetBinding
import com.solbios.databinding.FragmentHomeBinding
import com.solbios.interfaces.BrandClickListener
import com.solbios.interfaces.ItemClickListener
import com.solbios.model.home.*
import com.solbios.network.ApiState
import com.solbios.other._Address
import com.solbios.other._address
import com.solbios.other.isNetworkAvailable
import com.solbios.other.toast
import com.solbios.ui.adapter.FeatureBrandAdapter
import com.solbios.ui.adapter.PopularCategoriesAdapter
import com.solbios.ui.viewModel.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment() ,ItemClickListener ,BrandClickListener{

    var binding: FragmentHomeBinding?=null
   private val popularCategoriesList = mutableListOf<Category>()
   private val featureBrandsArrayList = mutableListOf<Brand>()
   private val  bannerList = mutableListOf<Banner>()
   private  val viewModel: HomeViewModel by viewModels()
    var sessionManagement: SessionManagement?=null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var noBinding: ActivityNoInternetBinding?=null
    private lateinit var locationRequest: LocationRequest


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

            binding = FragmentHomeBinding.inflate(layoutInflater)
            binding?.viewModel = viewModel
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManagement= context?.let { SessionManagement(it) }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        getHomeDetails()
        startJob()
        setLocationRequest()
        //locationPermission()
       // requireActivity().startService(Intent(context, ForegroundOnlyLocationService::class.java))


    }
    private fun setLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(5000)
        locationRequest.setFastestInterval(2000)
        getCurrentLocation()
    }



    private fun getHomeDetails(){
      if(context?.let { isNetworkAvailable(it) }==true){
          viewModel.getHomeDetails("Bearer"+" "+sessionManagement?.getToken())
      }else{
          toast(context)
      }
  }



  private fun locationPermission() {
          val locationPermissionRequest = registerForActivityResult(
              ActivityResultContracts.RequestMultiplePermissions()
          ) { permissions ->
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                  when {
                          permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                              Log.e("Location","ACCESS_FINE_LOCATION")
                              getLocation()
                          }
                          permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                              Log.e("Location","ACCESS_COARSE_LOCATION")

                          } else -> {
                          Log.e("Location","else")

                      }
                  }
              }
          }

          locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION))
      }


      fun getLocation(): Boolean {
          if (context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)} != PackageManager.PERMISSION_GRANTED &&context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION)}!= PackageManager.PERMISSION_GRANTED){
             Toast.makeText(context,"permission",Toast.LENGTH_LONG).show()
              Log.e("Location","permission")
          }
          else{
              fusedLocationClient.lastLocation
                  .addOnSuccessListener { location : Location? ->
                      val geocoder=Geocoder(context, Locale.getDefault())
                      val address: MutableList<Address>? =
                          location?.latitude?.let { geocoder.getFromLocation(it, location?.longitude, 1) }
                      val subLocality= address?.get(0)?.subLocality
                      val city=address?.get(0)?.locality
                      val state=address?.get(0)?.adminArea
                      locationTextView?.text=subLocality+","+city+","+state

                  }
          }
         return true
      }




    private  lateinit var homeJob : Job
    fun updateUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {
            }
            ApiState.Loading -> {
            }
            is ApiState.Success<*> -> {
                (state.data as? PopularCategoriesRoot)?.let {
                    popularCategoriesList.clear()
                    popularCategoriesList.addAll(it.data.categories)
                    featureBrandsArrayList.clear()
                    featureBrandsArrayList.addAll(it.data.brands)
                    bannerList.clear()
                    bannerList.addAll(it.data.banners)
                    setPopularCategoriesAdapter(popularCategoriesList)
                    setFeatureBrandsAdapter(featureBrandsArrayList)
                    setBannerList(bannerList)
                    setBadge(it.cart_total)

                }


            }

        }
    }

    private fun setToolbar() {
        cartImageView.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_cartFragment)
        }
        //locationTextView?.text="Solbios"

    }

    override fun onResume() {
        super.onResume()
    }

    private fun setBadge(cartTotal: Int) {
        if (cartTotal==0){
            cart_badge.visibility=View.GONE
        }else{
            if(cartTotal!=null) {
                cart_badge.text = cartTotal.toString()
                cart_badge.visibility=View.VISIBLE

            }
        }
        setToolbar()

    }

    private fun startJob() {
        homeJob =  lifecycleScope.launch {
            viewModel.apiState.collect {
                updateUi(it)

            }
        }

        homeJob.start()
    }


    private fun setFeatureBrandsAdapter(list:List<Brand>) {
        val adapter=FeatureBrandAdapter(list,this)
       binding?.brandsRecyclerView?.adapter=adapter
    }


    private fun setPopularCategoriesAdapter(list:List<Category>) {
        val adapter=PopularCategoriesAdapter(list,this)
        binding?.popularCategoriesRecyclerView?.adapter=adapter
    }




   private fun setBannerList(list:List<Banner>) {
     val  bannerAdapter = context?.let { HomeBannerAdapter(list, it) }
       val linearLayoutManager = LinearLayoutManager(context)
       linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
       binding?.pager?.adapter = bannerAdapter
      binding?. indicator?.setViewPager(pager)

   }

    override fun onViewClicked(view: View, position: Int) {
        val action= HomeFragmentDirections.actionHomeFragmentToProductListFragment( "",popularCategoriesList[position].id.toString())
       view.findNavController().navigate(action)
    }

    override fun onBrandViewClicked(view: View, position: Int) {
        val action= HomeFragmentDirections.actionHomeFragmentToProductListFragment( featureBrandsArrayList[position].id.toString(),"")
        view.findNavController().navigate(action)
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    getCurrentLocation()
                } else {
                    turnOnGPS()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                getCurrentLocation()
            }
        }
    }

    private fun getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context?.let {
                    ActivityCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } == PackageManager.PERMISSION_GRANTED
            ) {
                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(context)
                        .requestLocationUpdates(locationRequest, object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                super.onLocationResult(locationResult)
                                LocationServices.getFusedLocationProviderClient(context)
                                    .removeLocationUpdates(this)
                                if (locationResult != null && locationResult.locations.size > 0) {
                                    val index = locationResult.locations.size - 1
                                    val latitude = locationResult.locations[index].latitude
                                    val longitude = locationResult.locations[index].longitude
                                    val gcd = Geocoder(
                                        context,
                                        Locale.getDefault()
                                    )
                                    val addresses: List<Address>
                                    try {
                                        addresses = gcd.getFromLocation(latitude, longitude, 1)
                                        if (addresses.size > 0) {
                                            val address =
                                                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                            val locality = addresses[0].locality
                                            val subLocality = addresses[0].subLocality
                                            val state = addresses[0].adminArea
                                            val country = addresses[0].countryName
                                            val postalCode = addresses[0].postalCode
                                            val knownName = addresses[0].featureName
                                            if (subLocality != null) {

                                                // currentLocation = locality + "," + subLocality;
                                                Log.e("Tag","Latitude: $latitude\nLongitude: $longitude\n$locality,$subLocality,$state")
                                                locationTextView?.text=subLocality+","+locality+","+state

                                            } else {

                                                //   currentLocation = locality;
                                            }
                                            // current_locality = locality;
                                        }
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                    //AddressText.setText("Latitude: "+ latitude + "\n" + "Longitude: "+ longitude);
                                }
                            }
                        }, Looper.getMainLooper())
                } else {
                    turnOnGPS()
                }
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
    }

    private fun turnOnGPS() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result = LocationServices.getSettingsClient(
            context
        )
            .checkLocationSettings(builder.build())
        result.addOnCompleteListener { task ->
            try {
                val response =
                    task.getResult(ApiException::class.java)
                Toast.makeText(context, "GPS is already tured on", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(activity, 2)
                    } catch (ex: IntentSender.SendIntentException) {
                        ex.printStackTrace()
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }
    private fun isGPSEnabled(): Boolean {
        var locationManager: LocationManager? = null
        var isEnabled = false
        if (locationManager == null) {
            locationManager =context?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return isEnabled
    }

}