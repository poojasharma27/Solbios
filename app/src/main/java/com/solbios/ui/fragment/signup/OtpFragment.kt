package com.solbios.ui.fragment.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.firstapp.sharedPreference.SessionManagement
import com.solbios.databinding.FragmentOtpBinding
import com.solbios.model.OtpData
import com.solbios.network.ApiState
import com.solbios.other.Constants.Enter_otp
import com.solbios.other.Constants.Otp
import com.solbios.ui.viewModel.authviewmodel.OtpViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_otp.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OtpFragment : Fragment() {

  private var binding:FragmentOtpBinding?=null
    private val viewModel: OtpViewModel by viewModels()
    lateinit var sessionManagement: SessionManagement
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentOtpBinding.inflate(layoutInflater)
        binding?.viewModel=viewModel
        startJob()
        errorThrow()
        validation()
        return binding?.root
    }


    private fun validation() {
        viewModel.validation.observe(viewLifecycleOwner, Observer {
            if (it==Otp){
                otpTextInputEditText.setError(Enter_otp)
            }
        })
    }

    private fun errorThrow() {
        viewModel.errorThrow.observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity,""+it, Toast.LENGTH_SHORT).show();

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       sessionManagement= context?.let { SessionManagement(it) }!!
        viewModel.timerCounter()
        resendTextView.isEnabled=false
        validationCounter()
    }

    private fun validationCounter() {
        viewModel.time.observe(viewLifecycleOwner, Observer {
            if (it=="00:00"){
                resendTextView.isEnabled=true

            }
        })

    }

    lateinit var otpJob : Job
    fun updateUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {

            }
            ApiState.Loading -> {

            }
            is ApiState.Success<*> -> {
                (state.data as? OtpData)?.let {
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
        otpJob =  lifecycleScope.launch {
            viewModel.apiState.collect {
                updateUi(it)

            }
        }

        otpJob.start()
    }
}