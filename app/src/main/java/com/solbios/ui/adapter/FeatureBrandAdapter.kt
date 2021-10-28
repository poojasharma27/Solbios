package com.solbios.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.solbios.databinding.ItemFeaturedBrandsBinding
import com.solbios.interfaces.BrandClickListener
import com.solbios.interfaces.ItemClickListener
import com.solbios.model.home.Brand

class FeatureBrandAdapter(private val list:List<Brand>,val itemClickListener: BrandClickListener) :
    RecyclerView.Adapter<FeatureBrandAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=ItemFeaturedBrandsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FeatureBrandAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.viewModel=list[position]
        holder.binding.brandRelativeLayout.setOnClickListener {
            itemClickListener.onBrandViewClicked(it,position)

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: ItemFeaturedBrandsBinding) : RecyclerView.ViewHolder(binding.root)


}