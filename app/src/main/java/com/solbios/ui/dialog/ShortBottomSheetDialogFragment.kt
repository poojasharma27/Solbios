package com.solbios.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.solbios.databinding.LayoutShortBottomSheetBinding
import com.solbios.other.internetCheck
import kotlinx.android.synthetic.main.layout_filter_bottom_sheet.*
import kotlinx.android.synthetic.main.layout_filter_bottom_sheet.clearImageView
import kotlinx.android.synthetic.main.layout_short_bottom_sheet.*

class ShortBottomSheetDialogFragment( private  var sortListener: SortListener): BottomSheetDialogFragment() {

    companion object {

        const val TAG = "CustomBottomSheetDialogFragment"

    }
    private  var binding: LayoutShortBottomSheetBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= LayoutShortBottomSheetBinding.inflate(layoutInflater)
        internetCheck(context)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crossOnClickListener()
        lowToHighRadioButton.setOnClickListener{
            sortListener.onLowToHigh(it,"asc")
            dialog?.dismiss()

        }
        highToLowRadioButton.setOnClickListener {
            sortListener.onHighToLow(it,"desc")
            dialog?.dismiss()
        }
    }

    private fun crossOnClickListener() {
        clearImageView.setOnClickListener {
            dialog?.dismiss()
        }
    }

    interface SortListener{

        fun onLowToHigh(view: View,descending:String);
        fun onHighToLow(view: View, ascending:String);
    }
}