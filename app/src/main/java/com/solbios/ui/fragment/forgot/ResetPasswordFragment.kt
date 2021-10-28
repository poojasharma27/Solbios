package com.solbios.ui.fragment.forgot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.solbios.databinding.FragmentResetPasswordBinding
import com.solbios.other.Constants
import com.solbios.other.Constants.Enter_confirmPassword
import com.solbios.ui.viewModel.authviewmodel.ResetPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_reset_password.*

@AndroidEntryPoint
class ResetPasswordFragment :Fragment() {

    private var binding:FragmentResetPasswordBinding?=null
    private val viewModel: ResetPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentResetPasswordBinding.inflate(layoutInflater)
        binding?.viewModel=viewModel
        errorThrow()
        validation()
        return binding?.root
    }

    private fun validation() {
        viewModel.validation.observe(viewLifecycleOwner, Observer {
            if (it==Constants.password){
                newPasswordTextInputEditText.setError(Constants.Enter_password)
                //newPasswordTextInputLayout.isEndIconVisible=false
            }else if (it==Constants.ConfirmPassword){
                confirmPasswordTextInputEditText.setError(Enter_confirmPassword)
            }
        })

    }

    private fun errorThrow() {
        viewModel.errorThrow.observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity,""+it, Toast.LENGTH_SHORT).show();

        })
    }

}