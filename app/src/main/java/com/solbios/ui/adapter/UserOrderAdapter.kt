package com.solbios.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.solbios.databinding.ItemYourOrdersBinding
import com.solbios.model.userOrder.Data
import java.text.DecimalFormat

class UserOrderAdapter( val list:List<Data>, val itemClickListener: ItemClickListener): RecyclerView.Adapter<UserOrderAdapter.ViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val binding=ItemYourOrdersBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserOrderAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.viewModel=list[position]
       var df =  DecimalFormat("0.00")
        holder.binding.tabletNameTextView.text="\u20B9"+ df.format(list[position].total_amount)
        holder.binding.yourOrderRelativeLayout.setOnClickListener {
            itemClickListener.OnItemClick(it,position)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }



    class ViewHolder(val binding:ItemYourOrdersBinding) :RecyclerView.ViewHolder(binding.root)


    interface ItemClickListener{
        fun OnItemClick(view: View, position: Int)
    }
}