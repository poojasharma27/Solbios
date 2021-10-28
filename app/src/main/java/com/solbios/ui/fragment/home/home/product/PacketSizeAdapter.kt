package com.solbios.ui.fragment.home.home.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.solbios.databinding.ItemPacketSizeBinding
import com.solbios.model.productDetails.PacketSize
import com.solbios.model.productDetails.SimilarProduct

class PacketSizeAdapter (val list: List<PacketSize>,val packetSizeListener: PacketSizeListener) : RecyclerView.Adapter<PacketSizeAdapter.ViewHolder>(){
   
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PacketSizeAdapter.ViewHolder {
        val binding=ItemPacketSizeBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PacketSizeAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PacketSizeAdapter.ViewHolder, position: Int) {
        holder.binding.packetSize=list[position]
        holder.binding.unitPacketTextView.setOnClickListener {
            packetSizeListener.onClickPacketSize(position)
           /* val action = ProductListDescriptionDirections.actionProductListDescriptionToProductListDescription(list[position].id)
            it.findNavController().navigate(action)*/
        }

    }

    override fun getItemCount(): Int {
         return list.size
    }
    class ViewHolder(val binding: ItemPacketSizeBinding) : RecyclerView.ViewHolder(binding.root){

    }
    interface PacketSizeListener{
        fun onClickPacketSize(position:Int)
    }

}