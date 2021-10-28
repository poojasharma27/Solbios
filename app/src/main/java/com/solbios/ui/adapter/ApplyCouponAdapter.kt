package com.solbios.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.solbios.databinding.ItemCouponBinding
import com.solbios.model.cart.applycoupon.ApplyCouponData

class ApplyCouponAdapter(val list:List<ApplyCouponData>,val onApplyClickListeners: OnApplyClickListeners) : RecyclerView.Adapter<ApplyCouponAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val binding= ItemCouponBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ApplyCouponAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.viewModel=list[position]
        holder.binding.applyTextView.setOnClickListener {
            onApplyClickListeners.onApplyCLickListeners(it,position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding:ItemCouponBinding):RecyclerView.ViewHolder(binding.root)


    interface OnApplyClickListeners{

        fun onApplyCLickListeners(view: View, position: Int)

    }
}