package com.solbios.ui.fragment.cart.orderSummary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.firstapp.sharedPreference.SessionManagement
import com.solbios.R
import com.solbios.databinding.FragmentOrderSummaryBinding
import com.solbios.model.orderSummary.CartData
import com.solbios.model.orderSummary.OrderSummaryRoot
import com.solbios.network.ApiState
import com.solbios.other.Constants
import com.solbios.other.internetCheck
import com.solbios.other.isNetworkAvailable
import com.solbios.other.toast
import com.solbios.ui.adapter.OrderSummaryAdapter
import com.solbios.ui.viewModel.home.orderSummary.OrderSummaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_order_summary.*
import kotlinx.android.synthetic.main.layout_toolbar_name.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderSummaryFragment : Fragment() {

   private  var binding:FragmentOrderSummaryBinding?=null
    private  val viewModel:OrderSummaryViewModel by viewModels()
   private var sessionManagement: SessionManagement?=null
    var itemTotalPrice:String=" "
    var toBePaidPrice:Double=0.0
    var taxAmount:Int=0
    var priceDiscount:String=" "
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding= FragmentOrderSummaryBinding.inflate(layoutInflater)
          binding?.viewModel=viewModel
        internetCheck(context)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        startJob()
        sessionManagement= context?.let { SessionManagement(it) }
        orderSummary()
    }

    fun orderSummary(){
        if( context?.let{ isNetworkAvailable(it) }==true) {
            viewModel.orderSummary("Bearer"+" "+sessionManagement?.getToken())

        }else{
            toast(context)
        }
    }

    private fun setToolbar() {
        backImageView.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }
        locationTextView.text = Constants.orderSummary

        confirmTextView.setOnClickListener {
            if( context?.let{ isNetworkAvailable(it) }==true) {
                val addressId: Int = viewModel.id!!
                Navigation.findNavController(it).navigate(OrderSummaryFragmentDirections.actionOrderSummaryFragmentToPaymentFragment(addressId, itemTotalPrice, priceDiscount, toBePaidPrice.toFloat(), taxAmount,viewModel.couponCode))
            }else{
                toast(context)
            }
        }
        changeTextView.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_orderSummaryFragment_to_selectAddressFragment)
        }

    }


    lateinit var orderSummaryJob : Job
    fun updateUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {

            }
            ApiState.Loading -> {

            }
            is ApiState.Success<*> -> {
                (state.data as? OrderSummaryRoot)?.let {
                   setAddres(it)
                    setAdapter(it.data.cart_data)
                itemTotalPrice=it.totalRealPrice.toString()
                    setToBePaid(it)

                taxAmount=it.tax_amount
                priceDiscount=it.totalRealPrice.minus(it.totalFinalPrice).toString()
                    //taxAmountValueTextView.text=it.tax_amount.toString()
                }


            }
        }
    }

    private fun setToBePaid(it: OrderSummaryRoot) {
        if (viewModel.couponCode?.discount_amount!=null){
            toBePaidPrice= it.totalFinalPrice.toDouble().minus(viewModel.couponCode?.discount_amount!!)
            pricesTextView.text= "\u20B9"+viewModel.couponCode?.discount_amount?.let { it1 -> it.totalFinalPrice.plus(it.tax_amount).minus(it1) }
            paidPricesTextView.text="\u20B9"+ viewModel.couponCode?.discount_amount?.let { it1 -> it.totalFinalPrice.plus(it.tax_amount).minus(it1) }
            couponCodeAmountTextView.text="-"+"\u20B9"+viewModel.couponCode?.discount_amount
            couponCodeValueTextView.text="("+viewModel.couponCode?.coupon_code+")"
        }else{
            toBePaidPrice= it.totalFinalPrice.toDouble()
            pricesTextView.text= "\u20B9"+ it.totalFinalPrice.plus(it.tax_amount)
            paidPricesTextView.text="\u20B9"+   it.totalFinalPrice.plus(it.tax_amount)
            couponCodeAmountTextView.visibility=View.GONE
            couponCodeValueTextView.visibility=View.GONE
            couponTextView.visibility=View.GONE

            }
    }

    private fun setAdapter(cartData: List<CartData>) {
     val adapter=OrderSummaryAdapter(cartData)
        orderItemRecyclerView.adapter=adapter
    }

    private fun setAddres(it: OrderSummaryRoot) {
        nameTextView.text=it.data.delivery_address.name
        addressTextView.text=it.data.delivery_address.address
        addressTypeTextView.text=it.data.delivery_address.format_address_type
        cityTextView.text=it.data.delivery_address.city
        stateTextView.text=it.data.delivery_address.state
        numberTextView.text= it.data.delivery_address.contact_number.toString()
        itemPricesTextView.text= "\u20B9"+it.totalRealPrice.toString()

        discountPriceTextView.text="_"+"\u20B9"+it.totalRealPrice.minus(it.totalFinalPrice).toString()
        taxValueTextView.text="+"+"\u20B9"+it.tax_amount.toString()

    }


    private fun startJob() {
        orderSummaryJob =  lifecycleScope.launch {
            viewModel.apiState.collect {
                updateUi(it)

            }



        }

        orderSummaryJob.start()
    }


}