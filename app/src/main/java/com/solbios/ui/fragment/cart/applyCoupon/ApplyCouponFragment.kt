package com.solbios.ui.fragment.cart.applyCoupon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.firstapp.sharedPreference.SessionManagement
import com.solbios.databinding.FragmentApplyCouponBinding
import com.solbios.model.cart.applycoupon.ApplyCouponData
import com.solbios.model.cart.applycoupon.ApplyCouponRoot
import com.solbios.model.cart.applycoupon.CouponRoot
import com.solbios.network.ApiState
import com.solbios.other.Constants
import com.solbios.ui.adapter.ApplyCouponAdapter
import com.solbios.ui.viewModel.home.cart.ApplyCouponViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_apply_coupon.*
import kotlinx.android.synthetic.main.layout_toolbar_name.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ApplyCouponFragment  : Fragment() ,ApplyCouponAdapter.OnApplyClickListeners{

  private  var binding: FragmentApplyCouponBinding?=null
    private  val viewModel:ApplyCouponViewModel by viewModels()
    var sessionManagement: SessionManagement?=null
    var applyCouponList= mutableListOf<ApplyCouponData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding= FragmentApplyCouponBinding.inflate(layoutInflater)
        binding?.viewModel=viewModel
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManagement= context?.let { SessionManagement(it) }
        viewModel.getApplyCoupon("Bearer"+" "+sessionManagement?.getToken())
        startJob()
        setToolBar()
        startCouponJob()
        errorThrow()
        applyForSearch()
    }

    private fun applyForSearch() {
    applyCouponTextView.setOnClickListener {
        var search=searchViewEditText.text.toString()
        if (search!=""){
            viewModel.getCoupon("Bearer"+" "+sessionManagement?.getToken(),searchViewEditText.text.toString())
        }else{
           Toast.makeText(context,"Please Enter Coupon Code",Toast.LENGTH_LONG).show()

        }
    }
    }

    private fun setToolBar() {
        backImageView.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }
        locationTextView.text=Constants.applyCoupon
    }
    private fun errorThrow() {
        viewModel.errorThrow.observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity,""+it, Toast.LENGTH_SHORT).show();

        })
    }


    private  lateinit var applyCouponJob : Job
    fun updateUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {
            }
            ApiState.Loading -> {
            }
            is ApiState.Success<*> -> {
                (state.data as? ApplyCouponRoot)?.let {
                    applyCouponList.addAll(it.data)
                 setApplyCoupon(applyCouponList)

                }

            }

        }
    }

    private fun setApplyCoupon(applyCouponList: List<ApplyCouponData>) {
        val adapter=ApplyCouponAdapter(applyCouponList,this)
        couponRecyclerView.adapter=adapter
    }

    private fun startJob() {
        applyCouponJob =  lifecycleScope.launch {
            viewModel.apiState.collect {
                updateUi(it)

            }
        }

        applyCouponJob.start()
    }
    private  lateinit var couponJob : Job
  private  fun updateCouponUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {
            }
            ApiState.Loading -> {
            }
            is ApiState.Success<*> -> {
                (state.data as? CouponRoot)?.let {
                    Log.e("coupon",it.data.discount_amount.toString())
                }


            }

        }
    }
    private fun startCouponJob(){
        couponJob=lifecycleScope.launch {
            viewModel.apiStateCoupon.collect {
                updateCouponUi(it)
            }
        }

    }
    override fun onApplyCLickListeners(view: View, position: Int) {
        viewModel.getCoupon("Bearer"+" "+sessionManagement?.getToken(),applyCouponList[position].coupon_code)
    }


}