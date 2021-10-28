package com.solbios.ui.fragment.cart.address

import android.os.Bundle
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
import com.solbios.databinding.FragmentAddAddressBinding
import com.solbios.model.addAddress.AddAddressRoot
import com.solbios.network.ApiState
import com.solbios.other.Constants
import com.solbios.other.Constants.addAddress
import com.solbios.ui.viewModel.home.address.AddAddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_address.*
import kotlinx.android.synthetic.main.fragment_add_address.nameTextInputEditText
import kotlinx.android.synthetic.main.fragment_add_address.phoneNumberTextInputEditText
import kotlinx.android.synthetic.main.layout_toolbar_name.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddAddressFragment :  Fragment() {

    private var binding:FragmentAddAddressBinding?=null
   private val viewModel: AddAddressViewModel by viewModels()
    var sessionManagement: SessionManagement?=null
    var addressType:Int?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAddAddressBinding.inflate(layoutInflater)
        binding?.viewModel=viewModel

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManagement= context?.let { SessionManagement(it) }
      validation()
        setToolbar()
       errorThrow()
        startJob()
        viewModel.userAddressDetails("Bearer"+" "+sessionManagement?.getToken())

        saveButton.setOnClickListener {
            setRadioGroup()
            viewModel.setAddress(it,"Bearer"+" "+sessionManagement?.getToken(),addressType)
        }

    }
    private fun errorThrow() {
        viewModel.errorThrow.observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity,""+it, Toast.LENGTH_SHORT).show();

        })
    }
    private fun validation() {
        viewModel.validation.observe(viewLifecycleOwner, Observer {
            if (it== Constants.name){
                nameTextInputEditText.setError(Constants.Enter_name)
            }
            else  if (it== Constants.home){
                addressTextInputEditText.setError(Constants.Enter_address)
            }
            else if(it== Constants.mobileNumber){
                phoneNumberTextInputEditText.setError(Constants.Enter_mobile_number)

            }
            else if(it== Constants.pinCode){
                pinCodeTextInputEditText.setError(Constants.Enter_pinCode)
            }
            else if(it== Constants.city){
                cityTextInputEditText.setError(Constants.Enter_city)
            }
            else if(it== Constants.state){
                stateTextInputEditText.setError(Constants.Enter_state)
            }

        })    }


    private fun setRadioGroup() {
        if (homeRadioButton.isChecked){
            addressType=1
        }
        else if(officeRadioButton.isChecked){
            addressType=2

        }else if (otherRadioButton.isChecked){
            addressType=3

        }

    }

    private fun setToolbar() {
        locationTextView.text=addAddress
        backImageView.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }

    }
    private  lateinit var addAddressDetailsJob : Job
    fun updateUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {
            }
            ApiState.Loading -> {
            }
            is ApiState.Success<*> -> {
                (state.data as? AddAddressRoot)?.let {
                     nameTextInputEditText.setText(it.data.name)
                    addressTextInputEditText.setText(it.data.address)
                    cityTextInputEditText.setText(it.data.city)
                    phoneNumberTextInputEditText.setText(it.data.contact_number)
                    pinCodeTextInputEditText.setText(it.data.contact_number)
                    stateTextInputEditText.setText(it.data.state)
                    selectRadioButton(it.data.address_type)
                    saveButton.setText("UPDATE")

                }


            }

        }
    }

    private fun selectRadioButton(addressType: Int) {
        if (addressType==1){
            homeRadioButton.isChecked=true
        }else if(addressType==2){
            officeRadioButton.isChecked=true
        }else if(addressType==3){
            otherRadioButton.isChecked=true
        }

    }

    private fun startJob() {
        addAddressDetailsJob =  lifecycleScope.launch {
            viewModel.apiStateDetails.collect {
                updateUi(it)

            }
        }

        addAddressDetailsJob.start()
    }
}