package com.solbios.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.solbios.R
import com.solbios.databinding.ItemProductListBinding
import com.solbios.model.productList.Data
import com.solbios.ui.fragment.home.home.product.ProductListFragmentDirections

class ProductListAdapter(private val list :List<Data>,private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=ItemProductListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProductListAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.binding.productList=list[position]
        percentage(holder,position)

        holder.binding.productlistRelativeLayout.setOnClickListener {
            onClickItem(it,position)
        }
        addToCartOnClick(holder,position)
         if (list[position].cart_product!=null){
             holder.binding.addCartTextView.visibility=View.GONE
             holder.binding.countTextView.text=list[position].cart_product.quantity.toString()
             holder.binding.plusMinusLinearLayout.visibility=View.VISIBLE
         }


    }

    private fun percentage(holder: ProductListAdapter.ViewHolder, position: Int) {
        val offerPrice=  String.format("%.0f",list[position].sales_price)
        holder.binding.offerPriceTextView.text="\u20B9"+offerPrice

        val price=  String.format("%.0f",list[position].price)
        holder.binding.actualPriceTextView.text="\u20B9"+price
        holder.binding.actualPriceTextView.paintFlags= holder.binding.actualPriceTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


        if (list[position].price==list[position].sales_price) {
            holder.binding.percentOffTextView.visibility=View.INVISIBLE
            holder.binding.actualPriceTextView.visibility=View.INVISIBLE
        }else{
            val percentage=((list[position].price-list[position].sales_price)*100)/list[position].price
            val rounded = String.format("%.0f", percentage)
            holder.binding.percentOffTextView.text= "$rounded% off"
        }
    }


    private fun addToCartOnClick(holder: ProductListAdapter.ViewHolder, position: Int) {
        holder.binding.addCartTextView.setOnClickListener {

            holder.binding.addCartTextView.visibility=View.GONE
            holder.binding.plusMinusLinearLayout.visibility=View.VISIBLE
            holder.binding.countTextView.text="1"
            itemClickListener.addOnItem(1,position)

        }
        holder.binding.plusImageView.setOnClickListener {
            var count=  Integer. parseInt(holder.binding.countTextView.text.toString())
            count++
            holder.binding.countTextView.text= count.toString()
            itemClickListener.addOnItem(1,position)
        }
        holder.binding.minusImageView.setOnClickListener {
            var count=  Integer. parseInt(holder.binding.countTextView.text.toString())
            count--
            holder.binding.countTextView.text= count.toString()
            if (count==0){
                holder.binding.addCartTextView.visibility=View.VISIBLE
                holder.binding.plusMinusLinearLayout.visibility=View.GONE
            }
            itemClickListener.removeOnItem(2,position)

        }
    }


    private fun onClickItem(it: View, position: Int) {
        val action=ProductListFragmentDirections.actionProductListFragmentToProductListDescription(list[position].id)
       it.findNavController().navigate(action)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: ItemProductListBinding): RecyclerView.ViewHolder(binding.root)


    interface ItemClickListener{

        fun  addOnItem(action:Int?, position: Int)
        fun  removeOnItem(action: Int?,position: Int)

    }

}