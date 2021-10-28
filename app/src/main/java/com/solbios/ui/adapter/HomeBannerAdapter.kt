package com.demo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import androidx.viewpager.widget.ViewPager
import com.solbios.R
import com.solbios.model.home.Banner


class HomeBannerAdapter(private var images: List<Banner>, private var context: Context) :
    PagerAdapter() {



    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
    override fun getCount(): Int {
        return images.size
    }

 override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
     (container as ViewPager).removeView(`object` as View)
 }
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
     val imageLayout : View = LayoutInflater.from(context).inflate(R.layout.item_banner_image, container, false).also {
           val  imageView :ImageView = it.findViewById(R.id.imagebanner)
           Glide.with(context).load(images.get(position).image).into(imageView)
            container.addView(it)

        }
        return imageLayout !!

    }


}