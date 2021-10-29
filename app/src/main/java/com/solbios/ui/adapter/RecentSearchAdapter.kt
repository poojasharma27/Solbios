package com.solbios.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.solbios.databinding.ItemRecentSearchBinding
import com.solbios.db.entities.SearchData

class RecentSearchAdapter(val list:List<SearchData>):RecyclerView.Adapter<RecentSearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentSearchAdapter.ViewHolder {
     val binding=ItemRecentSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RecentSearchAdapter.ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: RecentSearchAdapter.ViewHolder, position: Int) {
        holder.binding.viewModel=list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding:ItemRecentSearchBinding):RecyclerView.ViewHolder(binding.root) {

    }

}