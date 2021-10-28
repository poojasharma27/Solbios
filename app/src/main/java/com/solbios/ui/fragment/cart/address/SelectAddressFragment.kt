package com.solbios.ui.fragment.cart.address

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.firstapp.sharedPreference.SessionManagement
import com.solbios.R
import com.solbios.databinding.FragmentSelectAddressBinding
import com.solbios.model.selectAddress.Data
import com.solbios.model.selectAddress.SelectAddressRoot
import com.solbios.network.ApiState
import com.solbios.other.Constants.selectAddress
import com.solbios.ui.adapter.SelectAddressAdapter
import com.solbios.ui.viewModel.home.address.SelectAddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_select_address.*
import kotlinx.android.synthetic.main.layout_toolbar_name.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectAddressFragment : Fragment(),SelectAddressAdapter.OnItemClickListener{

  private var binding:FragmentSelectAddressBinding?=null
   private  val viewModel:SelectAddressViewModel by viewModels()
    var sessionManagement: SessionManagement?=null
    var selectAddressList= mutableListOf<Data>()
    var adapter:SelectAddressAdapter?=null
   private var addressPosition:Int?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSelectAddressBinding.inflate(layoutInflater)
          binding?.viewModel=viewModel
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManagement= context?.let { SessionManagement(it) }
        viewModel.selectAddress("Bearer"+" "+sessionManagement?.getToken())
        startJob()


        setToolbar()
    }

    private fun setToolbar() {
        backImageView.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }
        locationTextView.text=selectAddress
      addNewTextView.setOnClickListener {
          //addNewAddress(it,null)
       val action=SelectAddressFragmentDirections.actionSelectAddressFragmentToAddAddressFragment(null)
          it.findNavController().navigate(action)
      }
        continueTextView.setOnClickListener {
            if (addressPosition==null){
                Toast.makeText(activity,"Please select address",Toast.LENGTH_LONG).show()

            }else{
                val id =selectAddressList[addressPosition!!].id
                Navigation.findNavController(it).navigate(SelectAddressFragmentDirections.actionSelectAddressFragmentToOrderSummaryFragment(id))

            }

        }


    }

    private fun addNewAddress(it: View,position: Int) {
     val action=SelectAddressFragmentDirections.actionSelectAddressFragmentToAddAddressFragment(selectAddressList[position].id.toString())
          it.findNavController().navigate(action)

    }

    private  lateinit var selectAddressJob : Job
    fun updateUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {
            }
            ApiState.Loading -> {
            }
            is ApiState.Success<*> -> {
                (state.data as? SelectAddressRoot)?.let {
                    selectAddressList.clear()
                    selectAddressList.addAll(it.data)
                    setSelectAddress(selectAddressList)
                    priceTextView.text="\u20B9"+it.total_final_price.toString()
                }


            }

        }
    }

    private fun setSelectAddress(selectAddressList: List<Data>) {
     adapter=SelectAddressAdapter(selectAddressList,this)
        selectAddressRecyclerView.adapter=adapter
    }

    private fun startJob() {
        selectAddressJob =  lifecycleScope.launch {
            viewModel.apiState.collect {
                updateUi(it)

            }
        }

        selectAddressJob.start()
    }

    override fun onItemOptionMenu(v:View,position: Int) {
        setPopUpMenu(v,position)

    }

    override fun onAddressSelect(position: Int) {
        addressPosition=position
      //  selectAddressList[position].isSelected=!selectAddressList[position].isSelected
    }

    private fun setPopUpMenu(v: View, position: Int) {
        val popUpMenu=PopupMenu(v.context,v)
        popUpMenu.inflate(R.menu.option_menu)
        popUpMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem ->
            when(menuItem?.itemId){
                R.id.editItem->{
                    Log.e("TAG", "setPopUpMenu: ", )
                    addNewAddress(v,position)
                    Toast.makeText(activity,""+selectAddressList[position].id,Toast.LENGTH_LONG).show()
                }

                R.id.deleteItem->{
                    viewModel.deleteAddress("Bearer"+" "+sessionManagement?.getToken(),selectAddressList[position].id)
                    selectAddressList.removeAt(position)
                    adapter?.notifyDataSetChanged()

                }
            }
            true
        })
        popUpMenu.show()
    }



}