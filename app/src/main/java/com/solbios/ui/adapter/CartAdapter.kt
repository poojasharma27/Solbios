package com.solbios.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.solbios.R
import com.solbios.databinding.ItemTotalItemsBinding
import com.solbios.model.cart.Data

class CartAdapter( val list :List<Data>,val itemClickListener: ItemClickListener) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding= ItemTotalItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CartAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.binding.viewModel=list[position].get_product
        holder.binding.salePriceTextView.text="\u20B9"+list[position].total_product_price.toString()
        holder.binding.mrpTextView.paintFlags= holder.binding.mrpTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        holder.binding.countTextView.text=list[position].quantity.toString()

        if(list[position].quantity==1){
            holder.binding.minusImageView.setImageResource(R.drawable.remove)
            holder.binding.minusImageView.isEnabled=false
        }else{
            holder.binding.minusImageView.setImageResource(R.drawable.ic_baseline_indeterminate_check_box_24)
            holder.binding.minusImageView.isEnabled=true
        }
        holder.binding.plusImageView.setOnClickListener {
            var count=  Integer. parseInt(holder.binding.countTextView.text.toString())
            count++
            list[position].quantity=count
          holder.binding.countTextView.text= count.toString()
           // holder.binding.countTextView.text= list[position].quantity.toString()
            holder.binding.minusImageView.setImageResource(R.drawable.ic_baseline_indeterminate_check_box_24)
            holder.binding.minusImageView.isEnabled=true
            itemClickListener.addOnItem(1,position)
           val price=list[position].get_product.sales_price*count
            list[position].total_product_price=price
            val pay=list[position].total_product_price
            holder.binding.salePriceTextView.text="\u20B9"+price

        }
        holder.binding.minusImageView.setOnClickListener {
            var count=  Integer. parseInt(holder.binding.countTextView.text.toString())
            count--
            list[position].quantity=count
            holder.binding.countTextView.text= count.toString()

            if(count==1){
                holder.binding.minusImageView.setImageResource(R.drawable.remove)
                holder.binding.minusImageView.isEnabled=false
            }else{
                holder.binding.minusImageView.setImageResource(R.drawable.ic_baseline_indeterminate_check_box_24)
                holder.binding.minusImageView.isEnabled=true
            }
            itemClickListener.removeOnItem(2,position)
            val price=list[position].get_product.sales_price*count
            list[position].total_product_price=price
            holder.binding.salePriceTextView.text="\u20B9"+price

        }

        holder.binding.removeTextView.setOnClickListener {
           val pay=list[position].total_product_price
            itemClickListener.removeOnListItem(position,pay)

         /*   notifyItemRemoved(position)
            notifyItemRangeChanged(position, list.size)*/
        }

    }

    override fun getItemCount(): Int {
       return list.size
    }

    class ViewHolder(val binding: ItemTotalItemsBinding) : RecyclerView.ViewHolder(binding?.root)


    interface ItemClickListener{

        fun  addOnItem(action:Int?, position: Int)
        fun  removeOnItem(action: Int?,position: Int)
        fun removeOnListItem(position: Int,price:Int)
    }
}