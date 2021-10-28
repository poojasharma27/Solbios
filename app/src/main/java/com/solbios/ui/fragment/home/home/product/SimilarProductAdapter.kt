package com.solbios.ui.fragment.home.home.product

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.solbios.databinding.ItemSimilarProductBinding
import com.solbios.model.productDetails.SimilarProduct
import com.solbios.ui.adapter.ProductListAdapter

class SimilarProductAdapter(val list: List<SimilarProduct>) : RecyclerView.Adapter<SimilarProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SimilarProductAdapter.ViewHolder {
        val binding=
            ItemSimilarProductBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SimilarProductAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SimilarProductAdapter.ViewHolder, position: Int) {
        holder.binding.productList=list[position]
         val salePrice=list[position].sales_price
            val offerPrice=  String.format("%.0f",salePrice)
        holder.binding.offerPriceTextView.text="\u20B9"+offerPrice

        val price=list[position].price
            val rightPrice=  String.format("%.0f",price)
        holder.binding.offerPriceTextView.text="\u20B9"+rightPrice
        holder.binding.actualPriceTextView.paintFlags= holder.binding.actualPriceTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        val actualPrice=list[position].price
        val salesPrice = list[position].sales_price
        if (actualPrice==salesPrice) {
            holder.binding.percentOffTextView.visibility= View.INVISIBLE
            holder.binding.actualPriceTextView.visibility= View.INVISIBLE
        }else{
            val percentage=((actualPrice-salesPrice)*100)/actualPrice
            val rounded = String.format("%.0f", percentage)
            holder.binding.percentOffTextView.text= "$rounded% off"
        }

    }



    override fun getItemCount(): Int {
         return list.size
    }

    class ViewHolder(val binding: ItemSimilarProductBinding):RecyclerView.ViewHolder(binding?.root)

}