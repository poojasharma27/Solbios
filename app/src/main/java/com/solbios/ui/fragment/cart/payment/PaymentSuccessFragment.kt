package com.solbios.ui.fragment.cart.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.solbios.R
import com.solbios.databinding.FragmentPaymentSuccessBinding
import kotlinx.android.synthetic.main.fragment_payment_success.*

class PaymentSuccessFragment : Fragment()  {
   private  var binding:FragmentPaymentSuccessBinding?=null
   private  val args:PaymentSuccessFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentPaymentSuccessBinding.inflate(layoutInflater)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        yourIdTextView.text="Your OrderId:"+" "+args.orderId
        myOrdersTextView.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_paymentSuccessFragment_to_userOrderDetails)
        }
    }
}