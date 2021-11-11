package com.solbios.ui.fragment.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.solbios.databinding.FragmentProfileBinding
import kotlinx.android.synthetic.main.fragment_profile.*
import android.content.Intent
import android.widget.Toast
import androidx.navigation.Navigation
import com.firstapp.sharedPreference.SessionManagement
import com.solbios.R
import com.solbios.other.internetCheck
import com.solbios.other.isNetworkAvailable

import com.solbios.ui.AuthActivity


class ProfileFragment :Fragment() {

    private var binding:FragmentProfileBinding?=null
    var sessionManagement: SessionManagement?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding=FragmentProfileBinding.inflate(layoutInflater)
        internetCheck(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManagement= context?.let { SessionManagement(it) }
        setValues()
        onClickEvent()

    }

    private fun onClickEvent() {
        logoutTextView.setOnClickListener {
            if (context?.let { it1 -> isNetworkAvailable(it1) } ==true) {
                sessionManagement?.clearSharedPreference()
                Navigation.findNavController(it).navigate(R.id.action_profileFragment_to_auth)
            }else{
                Toast.makeText(context,"You are offline. Please check your internet connection ", Toast.LENGTH_LONG).show()

            }

        }

        myPharmacyOrderTextView.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_profileFragment_to_userOrderDetails)

        }

    }

    private fun setValues() {
        emailTextView.text=sessionManagement?.getUserEmail()
        hiTextView.text="Hi,"+sessionManagement?.getUserName()
    }
}