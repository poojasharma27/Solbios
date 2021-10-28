package com.solbios.ui.fragment.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.firstapp.sharedPreference.SessionManagement
import com.solbios.R
import com.solbios.databinding.FragmentSplashBinding

class SplashFragment :Fragment() {

    private var binding: FragmentSplashBinding?=null
    lateinit  var animation: Animation
    lateinit var sessionManagement: SessionManagement

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSplashBinding.inflate(layoutInflater)
        animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        binding?.logoImageView?.animation=animation
        sessionManagement= context?.let { SessionManagement(it) }!!

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openScreen()
    }

    private fun openScreen() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val s=sessionManagement.getUserId()
            if (s.isNullOrEmpty()){
            view?.let {
                Navigation.findNavController(it)
                    .navigate(R.id.action_splashFragment_to_auth)}
            }
            else{
                view?.let {
                Navigation.findNavController(it)
                    .navigate(R.id.action_splashFragment_to_dashboardFragment)
                }}

        },5000)

    }

    fun isUseId(userId:String?):Boolean{
        return userId?.isEmpty() == true && userId==null
    }
}