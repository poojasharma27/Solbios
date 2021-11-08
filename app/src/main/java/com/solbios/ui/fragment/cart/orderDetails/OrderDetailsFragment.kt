package com.solbios.ui.fragment.cart.orderDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.firstapp.sharedPreference.SessionManagement
import com.solbios.databinding.FragmentOrderDetailsBinding
import com.solbios.model.orderDetails.OrderDetailsRoot
import com.solbios.model.orderDetails.Product
import com.solbios.model.orderSummary.OrderSummaryRoot
import com.solbios.network.ApiState
import com.solbios.ui.adapter.OrderDetailsAdapter
import com.solbios.ui.viewModel.home.orderSummary.OrderDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_order_details.*
import kotlinx.android.synthetic.main.layout_toolbar_name.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderDetailsFragment : Fragment(){
    private var binding:FragmentOrderDetailsBinding?=null
    private val viewModel:OrderDetailsViewModel by viewModels()
    private var sessionManagement: SessionManagement?=null
    var productList= mutableListOf<Product>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      binding= FragmentOrderDetailsBinding.inflate(layoutInflater)
        binding?.viewModel=viewModel
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startJob()
        sessionManagement= context?.let { SessionManagement(it) }
        viewModel.getOrderDetails("Bearer"+" "+sessionManagement?.getToken())
        setToolbar()
    }

    private fun setToolbar() {
        locationTextView.text="Order Details"
        backImageView.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
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
                (state.data as? OrderDetailsRoot)?.let {
                 productList.clear()
                    productList.addAll(it.data.products)
                    setOrderDetails(productList)
                     setValues(it)
                }


            }
        }
    }

    private fun setValues(it: OrderDetailsRoot) {
        orderDeliveredTextView.text=it.data.current_status.title
        orderIdValueTextView.text=it.data.order_id
        orderIdsValueTextView.text=it.data.order_id
        transactionValueTextView.text=it.data.transaction_id
        paymentValueTextView.text=it.data.format_payment_type
        orderDateValueTextView.text=it.data.format_created_at
        deliveryOnTextView.text=it.data.format_created_at
        numberTextView.text=it.data.pickup_address.contact_number.toString()
        addressTextView.text=it.data.pickup_address.address
        cityTextView.text=it.data.pickup_address.city
        stateTextView.text=it.data.pickup_address.state
        shippingAddressValueTextView.text=it.data.pickup_address.name
        pinCodeTextView.text=it.data.pickup_address.pincode.toString()
        addressTypeTextView.text=it.data.pickup_address.format_address_type
        totalValueTextView.text="\u20B9"+it.data.total_amount.toString()
        subTotalValueTextView.text="\u20B9"+it.data.total_amount.toString()
        couponTotalValueTextView.text="\u20B9"+ 0
    }

    private fun setOrderDetails(productList: List<Product>) {
        val adapter=OrderDetailsAdapter(productList)
        itemsRecyclerView.adapter=adapter


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