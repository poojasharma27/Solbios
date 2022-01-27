package com.solbios.ui.fragment.cart.payment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_payment.*
import kotlinx.android.synthetic.main.layout_toolbar_name.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.firstapp.sharedPreference.SessionManagement
import com.solbios.R
import com.solbios.databinding.FragmentPaymentBinding
import com.solbios.model.orderId.OrderIdRoot
import com.solbios.model.paymentCreateOrder.CreateOrderIdRoot
import com.solbios.network.ApiState
import com.solbios.other.*
import com.solbios.ui.AuthActivity
import com.solbios.ui.viewModel.home.payment.PaymentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_order_summary.*
import kotlinx.android.synthetic.main.fragment_payment.discountPriceTextView
import kotlinx.android.synthetic.main.fragment_payment.itemPricesTextView
import kotlinx.android.synthetic.main.fragment_payment.paidPricesTextView
import kotlinx.android.synthetic.main.fragment_payment.pricesTextView
import kotlinx.android.synthetic.main.fragment_payment.taxValueTextView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentFragment : Fragment() {

  private var binding: FragmentPaymentBinding?=null
     val viewModel:PaymentViewModel by viewModels()
    private  val args: PaymentFragmentArgs by navArgs()
    var sessionManagement: SessionManagement?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentPaymentBinding.inflate(layoutInflater)
        binding?.viewModel=viewModel
        internetCheck(context)

        return binding?.root
    }
  private  val paymentSuccess: PaymentSuccessFragment?=null
   private val paymentFailure: PaymentFailureFragment?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(view)
        sessionManagement= context?.let { SessionManagement(it) }
        setArgsValue()

        addOnSuccess(view)
        addOnFailure(view)

    }

    private fun addOnFailure(view: View) {
    Failure.observe(viewLifecycleOwner){
       paymentFailure.apply {
           val orderId = it?.getString(Constants.orderId)
           val paymentId = it?.getString(Constants.paymentId)
           val reason = it?.getString(Constants.reason)
           val amount=args.toBePaid.plus(args.taxAmount).toString()

           if(orderId!=null) {
               viewModel.createOrderId(view, "Bearer" + " " + sessionManagement?.getToken(), amount, args.addressId, orderId, 2, paymentId, 2, reason
               ,args.taxAmount.toString())
           }  }
        it.clear()
    }
    }

    private fun addOnSuccess(view:View) {
        Success.observe(viewLifecycleOwner){
            paymentSuccess.apply {
                val orderId = it?.getString(Constants.orderId)
                val paymentId = it?.getString(Constants.paymentId)
                val amount=args.toBePaid.plus(args.taxAmount).toString()
                if(orderId!=null) {
                    viewModel.createOrderId(view, "Bearer" + " " + sessionManagement?.getToken(), amount, args.addressId, orderId, 2, paymentId, 1, "success",args.taxAmount.toString())
                }
            }
            it.clear()
        }    }

    private fun setArgsValue() {
        pricesTextView.text="\u20B9"+args.toBePaid.plus(args.taxAmount)
        itemPricesTextView.text="\u20B9"+args.itemTotal
        discountPriceTextView.text="_"+"\u20B9"+args.priceDiscount
        paidPricesTextView.text="\u20B9"+args.toBePaid.plus(args.taxAmount)
        taxValueTextView.text="+"+"\u20B9"+args.taxAmount
         setCouponAmount()
    }

    private fun setCouponAmount() {
        if (viewModel.couponCode?.discount_amount!=null){
            couponAmountValueTextView.text=getString(R.string.Rs)+viewModel.couponCode?.discount_amount
            couponCodeTextView.text="("+viewModel.couponCode?.coupon_code+")"
        }else{
            couponAmountValueTextView.visibility=View.GONE
            couponCodeTextView.visibility=View.GONE
            couponAmountTextView.visibility=View.GONE
        }
    }


    private fun setToolbar(view: View) {
        backImageView.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }
        locationTextView.text = Constants.paymentDetails
        confirmPaymentTextView.setOnClickListener {
            if( context?.let{ isNetworkAvailable(it) }==true) {
                setCheckRadioButton(view)
            }
            else{
                toast(context)
            }
           }
           startJob()
        startCreateOrderIdJob()

        }
    @SuppressLint("ResourceType")
    private fun setCheckRadioButton(view: View) {
        var amount=args.toBePaid.plus(args.taxAmount).toString()
        var token="Bearer"+" "+sessionManagement?.getToken()
        if(paymentRadioGroup.getCheckedRadioButtonId()<=0){
            Toast.makeText(activity,"Please Select payment Type",Toast.LENGTH_LONG).show()

        }else if(cashOnDeliveryRadioButton.isChecked){
            viewModel.createOrderId(view,token,amount,args.addressId,"",1,"",1,"success",args.taxAmount.toString())

        }else if(onlineDeliveryRadioButton.isChecked){
            viewModel.getOrderId(token,amount,args.taxAmount.toString())

        }

}




    lateinit var orderIdJob : Job
    fun updateUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {

            }
            ApiState.Loading -> {
                confirmPaymentTextView.isClickable=false
            }
            is ApiState.Success<*> -> {
                (state.data as? OrderIdRoot)?.let {
                    (activity as AuthActivity?)?.startPayment(it.data.id)
                    confirmPaymentTextView.isClickable=true

                }


            }
        }
    }
    private fun startJob() {
        orderIdJob =  lifecycleScope.launch {
            viewModel.apiState.collect {
                updateUi(it)
            }



        }

        orderIdJob.start()
    }
    lateinit var createOrderIdJob:  Job
    fun updateCreateOrderIdUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {

            }
            ApiState.Loading -> {
                confirmPaymentTextView.isClickable=false

            }
            is ApiState.Success<*> -> {
                (state.data as? CreateOrderIdRoot)?.let {
                    confirmPaymentTextView.isClickable=true

                }


            }
        }
    }
    private fun startCreateOrderIdJob() {
        createOrderIdJob =  lifecycleScope.launch {
            viewModel.apiStateOrderId.collect {
                updateCreateOrderIdUi(it)
            }



        }

        createOrderIdJob.start()
    }


}