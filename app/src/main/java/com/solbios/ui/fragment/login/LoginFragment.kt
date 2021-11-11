package com.solbios.ui.fragment.login

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
import com.solbios.R
import com.solbios.databinding.FragmentLoginBinding
import com.solbios.model.LoginData
import com.solbios.network.ApiState
import com.solbios.other.Constants
import com.solbios.other.Constants.Enter_password
import com.solbios.other.Constants.noInternet
import com.solbios.other.Constants.password
import com.solbios.other.internetCheck
import com.solbios.other.isNetworkAvailable
import com.solbios.ui.viewModel.authviewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.forgotTextView
import kotlinx.android.synthetic.main.fragment_login.phoneNumberTextInputEditText
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment  : Fragment() {

private var binding:FragmentLoginBinding?=null
 private val viewModel: LoginViewModel by viewModels()
 lateinit var sessionManagement:SessionManagement
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentLoginBinding.inflate(layoutInflater)
        binding?.viewModel=viewModel
        internetCheck(context)

        changeTextSpannable()

        validation()
        errorThrow()

        sessionManagement= context?.let { SessionManagement(it) }!!
        if( context?.let{isNetworkAvailable(it)}==true) {
            startJob()
        }
        else{
            Toast.makeText(context, noInternet,Toast.LENGTH_LONG).show()
        }
        return binding?.root
    }

    private fun validation() {
        viewModel.validation.observe(viewLifecycleOwner, Observer {
            if (it == Constants.mobileNumber) {
                phoneNumberTextInputEditText.setError(Constants.Enter_mobile_number)

            }
           else if(it==password){
                passTextInputEditText.setError(Enter_password)
            }

        })
    }

    private fun errorThrow() {
        viewModel.errorThrow.observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity,""+it,Toast.LENGTH_SHORT).show();

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        forgotTextView.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_forgotFragment)

        }
        signupTextView.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun changeTextSpannable() {
        val spannable = SpannableString("Do not have an Account? SignUp")
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#0096FF")),
            24, // start
            30, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        binding?.signupTextView?.text = spannable
    }

    lateinit var loginJob : Job
    fun updateUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {
            }
            ApiState.Loading -> {

            }
            is ApiState.Success<*> -> {
                (state.data as? LoginData)?.let {
                    sessionManagement.setUserName(it.name)
                    sessionManagement.setUserEmail(it.email)
                    sessionManagement.setUserMobileNumber(it.mobile_number)
                    sessionManagement.setUserId(java.lang.String.valueOf(it.id))
                    sessionManagement.setIsMobileVerify(java.lang.String.valueOf(it.is_mobile_verify))
                    sessionManagement.setStatus(java.lang.String.valueOf(it.status))
                    sessionManagement.setToken(java.lang.String.valueOf(it.token))

                }
            }

        }
    }

    private fun startJob() {
        loginJob =  lifecycleScope.launch {
            viewModel.apiState.collect {
                updateUi(it)

            }
        }

        loginJob.start()
    }

}