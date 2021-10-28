package com.solbios.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.solbios.databinding.ItemSelectAddressBinding
import com.solbios.model.selectAddress.Data
import android.widget.RadioButton

import android.widget.RadioGroup




class SelectAddressAdapter(val list: List<Data>,val onItemClickListener: OnItemClickListener):RecyclerView.Adapter<SelectAddressAdapter.ViewHolder>() {
    private var lastCheckedRB: RadioButton? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectAddressAdapter.ViewHolder {
        val binding=ItemSelectAddressBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SelectAddressAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectAddressAdapter.ViewHolder, position: Int) {
      holder.binding.viewModel=list[position]
      holder.binding.menuImageView.setOnClickListener {
          onItemClickListener.onItemOptionMenu(it,position)
      }

       // holder.binding.nameRadioButton.isChecked=list[position].isSelected
        holder.binding.addressTypeRadioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val checked_rb = group.findViewById<View>(checkedId) as RadioButton
            lastCheckedRB?.setChecked(false)
            //store the clicked radiobutton
            lastCheckedRB = checked_rb
        })
        holder.binding.nameRadioButton.setOnClickListener {
            onItemClickListener.onAddressSelect(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding:ItemSelectAddressBinding):RecyclerView.ViewHolder(binding?.root)

    interface OnItemClickListener{
        fun onItemOptionMenu(v: View, position: Int)
        fun onAddressSelect(position: Int)
    }
}