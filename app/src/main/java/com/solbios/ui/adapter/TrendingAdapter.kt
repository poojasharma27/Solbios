package com.solbios.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.solbios.databinding.ItemTrendingProductsBinding
import com.solbios.model.search.TrendingProduct

class TrendingAdapter(val list: List<TrendingProduct>):RecyclerView.Adapter<TrendingAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingAdapter.ViewHolder {
        val binding=ItemTrendingProductsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TrendingAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingAdapter.ViewHolder, position: Int) {
        holder.binding.viewModel=list[position]
    }


    override fun getItemCount(): Int {
      return list.size
    }
    class ViewHolder(val binding:ItemTrendingProductsBinding) : RecyclerView.ViewHolder(binding.root)

}