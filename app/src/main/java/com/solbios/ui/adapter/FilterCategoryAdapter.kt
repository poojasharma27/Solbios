package com.solbios.ui.adapter

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.solbios.databinding.ItemFilterCategoryBinding
import com.solbios.interfaces.ItemClickListener
import com.solbios.model.filter.Category

class FilterCategoryAdapter (private val  list:List<Category>, val itemClickListener: ItemClickListener) : RecyclerView.Adapter<FilterCategoryAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding= ItemFilterCategoryBinding.inflate(LayoutInflater.from(parent.context))
        return FilterCategoryAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.viewModel=list[position]
        holder.binding.checkbox.isChecked = list[position].selected
        list[position].selected=false
        holder.binding.checkbox.setOnClickListener {
            val pos =position
            itemClickListener.onViewClicked(it,pos)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder (val binding: ItemFilterCategoryBinding):RecyclerView.ViewHolder(binding?.root)


}