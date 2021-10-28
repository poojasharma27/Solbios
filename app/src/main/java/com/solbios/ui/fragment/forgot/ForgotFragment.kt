package com.solbios.ui.fragment.forgot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.solbios.R
import com.solbios.databinding.FragmentForgotBinding
import com.solbios.other.Constants
import com.solbios.ui.viewModel.authviewmodel.ForgotViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_forgot.*
import kotlinx.android.synthetic.main.fragment_forgot.phoneNumberTextInputEditText

@AndroidEntryPoint
class ForgotFragment :Fragment() {
    private var binding:FragmentForgotBinding?=null
    private val viewModel: ForgotViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotBinding.inflate(layoutInflater)
        binding?.viewModel=viewModel
        errorThorw()
        validation()
        return binding?.root
    }
    private fun validation() {
        viewModel.validation.observe(viewLifecycleOwner, Observer {
            if (it == Constants.mobileNumber) {
                phoneNumberTextInputEditText.setError(Constants.Enter_mobile_number)
            }
        })    }
    private fun errorThorw() {
        viewModel.errorThrow.observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity,""+it, Toast.LENGTH_SHORT).show();

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginTextView.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_forgotFragment_to_loginFragment)
        }
    }
}