package com.solbios.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.solbios.databinding.ItemOrderDetailsBinding
import com.solbios.model.orderDetails.Product

class OrderDetailsAdapter(val list :List<Product>) :RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderDetailsAdapter.ViewHolder {
      val binding=ItemOrderDetailsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OrderDetailsAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderDetailsAdapter.ViewHolder, position: Int) {
        holder.binding.viewModel=list[position]
        holder.binding.mrpTextView.paintFlags= holder.binding.mrpTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

          val salePrice= list[position].product_detail.sales_price * list[position].quantity
          val price= list[position].product_detail.price * list[position].quantity
        holder.binding.salePriceTextView.text="\u20b9"+salePrice.toString()
        holder.binding.mrpTextView.text="\u20b9"+price.toString()
    }

    override fun getItemCount(): Int {
       return list.size
    }

    class ViewHolder(val binding :ItemOrderDetailsBinding) : RecyclerView.ViewHolder(binding.root)



}