package com.solbios.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.solbios.databinding.ItemSearchBinding
import com.solbios.model.search.Data

class SearchAdapter( val list:List<Data>, val onSearchItemClickListener: OnSearchItemClickListener) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding= ItemSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SearchAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.viewModel=list[position]
        holder.binding.searchRelativeLayout.setOnClickListener {
            onSearchItemClickListener.searchItemClickListener(it,position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(val binding:ItemSearchBinding):RecyclerView.ViewHolder(binding?.root)

    interface OnSearchItemClickListener{

        fun searchItemClickListener(view: View, position: Int)

    }

}