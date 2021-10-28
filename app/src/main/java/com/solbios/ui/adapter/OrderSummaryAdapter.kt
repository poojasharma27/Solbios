package com.solbios.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.solbios.databinding.ItemOrderYourItemBinding
import com.solbios.model.orderSummary.CartData
import com.solbios.model.orderSummary.Data

class OrderSummaryAdapter(val list:List<CartData>) : RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=ItemOrderYourItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  OrderSummaryAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.viewModel=list[position].get_product
        holder.binding.mrpTextView.paintFlags= holder.binding.mrpTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: ItemOrderYourItemBinding):RecyclerView.ViewHolder(binding.root) {

    }
}