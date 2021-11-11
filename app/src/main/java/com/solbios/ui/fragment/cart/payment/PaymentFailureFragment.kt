package com.solbios.ui.fragment.cart.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.solbios.R
import com.solbios.databinding.FragmentPaymentFailureBinding
import com.solbios.other.internetCheck
import kotlinx.android.synthetic.main.fragment_payment_failure.*

class PaymentFailureFragment :Fragment() {

    private var binding:FragmentPaymentFailureBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentPaymentFailureBinding.inflate(layoutInflater)
        internetCheck(context)

        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myCartTextView.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_paymentFailureFragment_to_cartFragment)
        }
    }
}