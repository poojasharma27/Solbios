package com.solbios.other

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.solbios.R
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

import com.bumptech.glide.load.resource.bitmap.CenterCrop

import com.bumptech.glide.request.RequestOptions




@BindingAdapter("url")
fun image(view: ImageView, url: String?) {
    if (!(url.isNullOrEmpty())) {
        url?.apply {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(22))
            Glide.with(view).load("$url").apply(requestOptions).into(view)
            //Glide.with(view).load(R.drawable.dabur).into(view)
        }
    } else {
        Glide.with(view)
            .load(R.drawable.dabur).into(view)
    }

}

