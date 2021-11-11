package com.solbios.ui.fragment.signup

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.solbios.R
import com.solbios.databinding.FragmentRegisterBinding
import com.solbios.network.ApiState
import com.solbios.other.Constants.Enter_email_Id
import com.solbios.other.Constants.Enter_mobile_number
import com.solbios.other.Constants.Enter_name
import com.solbios.other.Constants.Enter_password
import com.solbios.other.Constants.email
import com.solbios.other.Constants.mobileNumber
import com.solbios.other.Constants.name
import com.solbios.other.Constants.password
import com.solbios.other.internetCheck
import com.solbios.ui.viewModel.authviewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var binding: FragmentRegisterBinding?=null
  //  private val viewModel:SignUpViewModel by viewModels()
    lateinit var viewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentRegisterBinding.inflate(layoutInflater)
        viewModel= ViewModelProvider(this).get(SignUpViewModel::class.java)
        binding?.viewModel=viewModel
        internetCheck(context)

        validation()

        errorThrow()
        changeTextSpannable()

        return binding?.root
    }

    private fun validation() {
        viewModel.validation.observe(viewLifecycleOwner, Observer {
            if (it==name){
                nameTextInputEditText.setError(Enter_name)
            }
           else  if (it==mobileNumber){
                phoneNumberTextInputEditText.setError(Enter_mobile_number)
            }
            else if(it==email){
                emailTextInputEditText.setError(Enter_email_Id)

            }
            else if(it== password){
                passwordTextInputEditText.setError(Enter_password)
            }

        })    }

    private fun errorThrow() {
        viewModel.errorThrow.observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity,""+it,Toast.LENGTH_SHORT).show();

        })    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
           startJob()
        signInTextView.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment)
        }

    }

  lateinit var newsJob : Job
    fun updateUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {

            }
            ApiState.Loading -> {

            }
            is ApiState.Success<*> -> {

            }

        }
    }

    private fun startJob() {
        newsJob =  lifecycleScope.launch {
         viewModel.apiState.collect {
             updateUi(it)
         }
        }

        newsJob.start()
    }

    private fun changeTextSpannable() {
        val spannable = SpannableString("Already have an Account? Login")
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#0096FF")),
            25, // start
            30, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        binding?.signInTextView?.text=spannable
    }

}