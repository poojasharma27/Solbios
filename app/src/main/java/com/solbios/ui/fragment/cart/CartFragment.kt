package com.solbios.ui.fragment.cart

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firstapp.sharedPreference.SessionManagement
import com.solbios.model.cart.CartRoot
import com.solbios.model.cart.Data
import com.solbios.network.ApiState
import com.solbios.ui.adapter.CartAdapter
import com.solbios.ui.viewModel.home.cart.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.coroutines.flow.collect
import android.content.DialogInterface
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.solbios.R
import com.solbios.databinding.FragmentCartBinding
import com.solbios.model.cart.deleteCartItem.DeleteCartItem
import com.solbios.other.Constants
import kotlinx.android.synthetic.main.layout_toolbar_with_search.*
import kotlinx.android.synthetic.main.layout_toolbar_without_cart.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CartFragment : Fragment(),CartAdapter.ItemClickListener {

  private  var binding: FragmentCartBinding?=null
   private  val viewModel:CartViewModel by viewModels()
    var sessionManagement: SessionManagement?=null
    val cartList= mutableListOf<Data>()
    var total = 0;
    var adapter:CartAdapter?=null
    var taxAmount=0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentCartBinding.inflate(layoutInflater)
        binding?.viewModel=viewModel
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManagement= context?.let { SessionManagement(it) }
        viewModel.getCartItem("Bearer"+" "+sessionManagement?.getToken())
        startJob()
        setToolbar()

          startDeleteItemJob()
    }



    private fun setToolbar() {
        locationTextViewCart.text=Constants.cart

        backImageViewCart.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }

        searchImageViewCart.setOnClickListener {
        }
        applyRelativeLayout.setOnClickListener {
            Navigation.findNavController(it).navigate(CartFragmentDirections.actionCartFragmentToApplyCouponFragment(total))
        }

        checkoutTextView.setOnClickListener {
            val action =CartFragmentDirections.actionCartFragmentToSelectAddressFragment()
            it.findNavController().navigate(action)
        }
    }

    lateinit var cartJob : Job
    fun updateUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {

            }
            ApiState.Loading -> {

            }
            is ApiState.Success<*> -> {
                (state.data as? CartRoot)?.let {
                    cartList.clear()
                    cartList.addAll(it.data)
                    setCartItem(cartList)
                    totalItemTextView.text = "Total Item (" + it.total.toString() + ")"
                    total=0

                    for ( i in 0 until  cartList.size){

                        total=total.plus(cartList[i].total_product_price)

                    }

                    itemPriceTextView.text="\u20B9"+total.toString()

                    priceTextView.text= "\u20B9"+total
                    paidPriceTextView.text="\u20B9"+total
                }


            }
        }
    }

    private fun setCartItem(cartList: List<Data>) {
      adapter= CartAdapter(cartList,this)
        cartItemRecyclerView.adapter=adapter
    }

    private fun startJob() {
        cartJob =  lifecycleScope.launch {
            viewModel.apiState.collect {
                updateUi(it)

            }



        }

        cartJob.start()
    }
    lateinit var deleteItemJob : Job
    fun updateDeleteItemUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {

            }
            ApiState.Loading -> {

            }
            is ApiState.Success<*> -> {
                (state.data as? DeleteCartItem)?.let {
                    totalItemTextView.text = "Total Item (" + it.data.cart_total.toString() + ")"

                }


            }
        }
    }
    private fun startDeleteItemJob() {
        deleteItemJob =  lifecycleScope.launch {
            viewModel.apiStateDelete.collect {
                updateDeleteItemUi(it)

            }



        }

        deleteItemJob.start()
    }

    override fun addOnItem(action: Int?, position: Int) {
        viewModel.getAddToCart("Bearer"+" "+sessionManagement?.getToken(),cartList[position].product_id,action)
       total= total.plus(cartList[position].get_product.sales_price)
        itemPriceTextView.text="\u20B9"+total
        priceTextView.text= "\u20B9"+total
        paidPriceTextView.text="\u20B9"+total
    }

    override fun removeOnItem(action: Int?, position: Int) {
        viewModel.getAddToCart("Bearer"+" "+sessionManagement?.getToken(),cartList[position].product_id,action)
        total= total.minus(cartList[position].get_product.sales_price)
        itemPriceTextView.text="\u20B9"+total
        priceTextView.text= "\u20B9"+total
        paidPriceTextView.text="\u20B9"+total


    }

    override fun removeOnListItem(position: Int,price:Int) {
        val alertDialogBuilder =  AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle("Remove")
        alertDialogBuilder.setMessage("Are you sure, You wanted to remove ?")
              alertDialogBuilder.setPositiveButton("yes",DialogInterface.OnClickListener { dialogInterface, i ->
                  startDeleteItemJob()
                  viewModel.deleteCartItem("Bearer"+" "+sessionManagement?.getToken(),cartList[position].id)
                  cartList.removeAt(position)
                  adapter?.notifyDataSetChanged()
                 // viewModel.getCartItem("Bearer"+" "+sessionManagement?.getToken())
                   total=total.minus(price)
                  priceTextView.text= "\u20B9"+total
                  itemPriceTextView.text="\u20B9"+total
                  paidPriceTextView.text="\u20B9"+total
               //



              })

            alertDialogBuilder.setNegativeButton("No",DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })
        val alertDialog:AlertDialog = alertDialogBuilder.create();

        alertDialog.show()

    }


}