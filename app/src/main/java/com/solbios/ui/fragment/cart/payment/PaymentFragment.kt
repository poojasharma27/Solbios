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
import com.solbios.other.Constants
import kotlinx.android.synthetic.main.fragment_payment.*
import kotlinx.android.synthetic.main.layout_toolbar_name.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.firstapp.sharedPreference.SessionManagement
import com.solbios.databinding.FragmentPaymentBinding
import com.solbios.model.orderId.OrderIdRoot
import com.solbios.model.paymentCreateOrder.CreateOrderIdRoot
import com.solbios.network.ApiState
import com.solbios.other.Failure
import com.solbios.other.Success
import com.solbios.ui.AuthActivity
import com.solbios.ui.viewModel.home.payment.PaymentViewModel
import dagger.hilt.android.AndroidEntryPoint
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
           if(orderId!=null) {
               viewModel.createOrderId(view, "Bearer" + " " + sessionManagement?.getToken(), args.toBePaid.toString(), args.addressId, orderId, 2, paymentId, 2, reason
               )
           }  }
        it.clear()
    }
    }

    private fun addOnSuccess(view:View) {
        Success.observe(viewLifecycleOwner){
            paymentSuccess.apply {
                val orderId = it?.getString(Constants.orderId)
                val paymentId = it?.getString(Constants.paymentId)
                if(orderId!=null) {
                    viewModel.createOrderId(view, "Bearer" + " " + sessionManagement?.getToken(), args.toBePaid.toString(), args.addressId, orderId, 2, paymentId, 1, "success")
                }
            }
            it.clear()
        }    }

    private fun setArgsValue() {
        pricesTextView.text="\u20B9"+args.toBePaid
        itemPricesTextView.text="\u20B9"+args.itemTotal
        discountPriceTextView.text="\u20B9"+args.priceDiscount
        paidPricesTextView.text="\u20B9"+args.toBePaid
    }


    private fun setToolbar(view: View) {
        backImageView.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }
        locationTextView.text = Constants.paymentDetails
        confirmPaymentTextView.setOnClickListener {
            setCheckRadioButton(view)
           }
           startJob()
        startCreateOrderIdJob()

        }
    @SuppressLint("ResourceType")
    private fun setCheckRadioButton(view: View) {
        var amount=args.toBePaid.toString()
        var token="Bearer"+" "+sessionManagement?.getToken()
        if(paymentRadioGroup.getCheckedRadioButtonId()<=0){
            Toast.makeText(activity,"Please Select payment Type",Toast.LENGTH_LONG).show()

        }else if(cashOnDeliveryRadioButton.isChecked){
            viewModel.createOrderId(view,token,amount,args.addressId,"",1,"",1,"success")

        }else if(onlineDeliveryRadioButton.isChecked){
            viewModel.getOrderId(token,amount)

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