package com.solbios.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.solbios.R
import com.solbios.databinding.ItemPopularCategoriesBinding
import com.solbios.interfaces.ItemClickListener
import com.solbios.model.PopularCategoriesModel
import com.solbios.model.home.Category
import com.solbios.model.home.PopularCategoriesRoot

class PopularCategoriesAdapter(private var list:List<Category>, val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<PopularCategoriesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPopularCategoriesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PopularCategoriesAdapter.ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.viewModel=list[position]
        holder.binding.PopularCategoriesRelativeLayout.setOnClickListener {
        itemClickListener.onViewClicked(it,position)
         //   Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_productListFragment)

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }



    class ViewHolder (val binding: ItemPopularCategoriesBinding) : RecyclerView.ViewHolder(binding.root) {

    }



}