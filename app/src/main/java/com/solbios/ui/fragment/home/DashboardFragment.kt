package com.solbios.ui.fragment.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.solbios.R
import com.solbios.databinding.FragmentDashboardBinding
import com.solbios.ui.viewModel.home.DashboardViewModel
import kotlinx.android.synthetic.main.fragment_dashboard.*


class DashboardFragment  :  Fragment(){

    private var binding: FragmentDashboardBinding?=null
    private val viewModel: DashboardViewModel  by viewModels()
  lateinit var navController:NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentDashboardBinding.inflate(layoutInflater)
        binding?.viewModel=viewModel
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       setUpNavigation()
        bottomNavigationController()




    }

    override fun onStart() {
        super.onStart()
    }

   /* private fun addObservers(view: View) {
        if (viewModel.type=="SignUp"){

        }else {
            logoutState.observe(viewLifecycleOwner) {
                Navigation.findNavController(view)
                    .navigate(R.id.action_dashboardFragment_to_loginFragment)
            }
        }
    }*/

    private fun setUpNavigation() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_main_fragment) as NavHostFragment
        navController = navHostFragment.navController
        activity_main_bottom_navigation_view?.setupWithNavController(navController)
    }



    private fun bottomNavigationController(){
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.productListFragment ->{ activity_main_bottom_navigation_view.visibility =
                    View.GONE}

                R.id.searchFragment ->{activity_main_bottom_navigation_view.visibility =
                    View.GONE}
                R.id.customBottomSheetDialogFragment->{activity_main_bottom_navigation_view.visibility=View.GONE}
                R.id.filterBottomSheetDialogFragment->{
                    activity_main_bottom_navigation_view.visibility=View.GONE
                }
                R.id.productListDescription->{
                    activity_main_bottom_navigation_view.visibility=View.GONE
                }
                R.id.cartFragment->{
                    activity_main_bottom_navigation_view.visibility=View.GONE
                }
                R.id.applyCouponFragment->{
                    activity_main_bottom_navigation_view.visibility=View.GONE
                }
                R.id.selectAddressFragment->{
                    activity_main_bottom_navigation_view.visibility=View.GONE
                }
                R.id.addAddressFragment->{
                    activity_main_bottom_navigation_view.visibility=View.GONE
                }
                R.id.orderSummaryFragment->{
                    activity_main_bottom_navigation_view.visibility=View.GONE
                }
                R.id.paymentFragment->{
                    activity_main_bottom_navigation_view.visibility=View.GONE
                }
                R.id.loginFragment->{
                    activity_main_bottom_navigation_view.visibility=View.GONE
                }
                R.id.registerFragment->{
                    activity_main_bottom_navigation_view.visibility=View.GONE
                }
                R.id.otpFragment->{
                    activity_main_bottom_navigation_view.visibility=View.GONE
                }
                R.id.forgotFragment->{
                    activity_main_bottom_navigation_view.visibility=View.GONE
                }
                R.id.forgotOtpVerifyFragment->{
                    activity_main_bottom_navigation_view.visibility=View.GONE
                }
                R.id.paymentSuccessFragment->{
                    activity_main_bottom_navigation_view.visibility=View.GONE
                }
                R.id.userOrderDetails->{
                    activity_main_bottom_navigation_view.visibility=View.GONE
                }
                R.id.paymentFailureFragment->{
                    activity_main_bottom_navigation_view.visibility=View.GONE
                }
                R.id.orderDetailsFragment->{
                    activity_main_bottom_navigation_view.visibility=View.GONE
                }
                else -> {activity_main_bottom_navigation_view.visibility =
                    View.VISIBLE}


            }

        }
    }


}