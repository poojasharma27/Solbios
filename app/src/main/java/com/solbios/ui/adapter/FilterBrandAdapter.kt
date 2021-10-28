package com.solbios.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.solbios.databinding.ItemFilterBrandBinding
import com.solbios.databinding.ItemFilterCategoryBinding
import com.solbios.interfaces.BrandClickListener
import com.solbios.interfaces.BrandListClickListener
import com.solbios.interfaces.ItemClickListener
import com.solbios.model.filter.Brand
import com.solbios.model.filter.Category
import com.solbios.ui.dialog.FilterBottomSheetDialogFragment as FilterBottomSheetDialogFragment1

class FilterBrandAdapter(private val  list:List<Brand>, val itemClickListener: BrandClickListener) : RecyclerView.Adapter<FilterBrandAdapter.ViewHolder>(){
    val lists= ArrayList<Int>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding= ItemFilterBrandBinding.inflate(LayoutInflater.from(parent.context))
        return FilterBrandAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.viewModel=list[position]

        holder.binding.brandCheckBox.isChecked = list[position].selected
        list[position].selected=false
        holder.binding.brandCheckBox.setOnClickListener {
            itemClickListener.onBrandViewClicked(it,position)
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder (val binding: ItemFilterBrandBinding): RecyclerView.ViewHolder(binding?.root)


}