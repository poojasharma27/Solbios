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
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.solbios.R
import com.solbios.databinding.FragmentCartBinding
import com.solbios.db.AppDataBase
import com.solbios.db.entities.CouponDetails
import com.solbios.db.entities.SearchData
import com.solbios.model.cart.applycoupon.CouponData
import com.solbios.model.cart.deleteCartItem.DeleteCartItem
import com.solbios.other.Constants
import com.solbios.other.Constants.noInternet
import com.solbios.other.internetCheck
import com.solbios.other.isNetworkAvailable
import kotlinx.android.synthetic.main.layout_toolbar_with_search.*
import kotlinx.android.synthetic.main.layout_toolbar_without_cart.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CartFragment : Fragment(),CartAdapter.ItemClickListener {

  private  var binding: FragmentCartBinding?=null
   private  val viewModel:CartViewModel by viewModels()
    var sessionManagement: SessionManagement?=null
    val cartList= mutableListOf<Data>()
    var total = 0.0
    var adapter:CartAdapter?=null
    var taxAmount=0
    private  var applyCouponEntity: CouponDetails? =null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentCartBinding.inflate(layoutInflater)
        binding?.viewModel=viewModel
        internetCheck(context)
      Log.e("tag",viewModel.couponCode?.coupon_id.toString())
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
            Navigation.findNavController(it).navigate(R.id.action_cartFragment_to_searchFragment)
        }
        applyRelativeLayout.setOnClickListener {
            Navigation.findNavController(it).navigate(CartFragmentDirections.actionCartFragmentToApplyCouponFragment(total.toInt()))
        }

        checkoutTextView.setOnClickListener {
            if (context?.let { it1 -> isNetworkAvailable(it1) } ==true) {
               val applyCoupon: CouponData? = applyCouponEntity?.let { it1 -> from(it1) }
                val action = CartFragmentDirections.actionCartFragmentToSelectAddressFragment(applyCoupon)
                it.findNavController().navigate(action)
            }else{
                Toast.makeText(context,noInternet,Toast.LENGTH_LONG).show()
            }
        }

        couponCodeRemoveTextView.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                 AppDataBase.invoke(context)?.searchDetailsDao()?.deleteAllCoupon()
            }
           applyCouponEntity ?.discount_amount=0.0
            applyCouponEntity?.coupon_id=0
            applyCouponEntity?.total_discounted_amount=0.0
            applyCouponEntity?.coupon_code=""
            applyCouponEntity?.coupon_title=""
            couponCodeTextView.visibility=View.GONE
            couponCodeRemoveTextView.visibility=View.GONE
            couponAmountTextView.visibility =View.GONE
            couponAmountValueTextView.visibility =View.GONE
            couponAmountValueTextView.text ="-" + "\u20B9" + 0
            priceTextView.text = "\u20B9" + total
            paidPriceTextView.text = "\u20B9" + total
        }
    }

    fun from(form: CouponDetails):CouponData =
        CouponData(
            form.discount_amount,
            form.total_discounted_amount,
            form.coupon_id,
            form.coupon_title,
            form.coupon_code,
            form.minimum_price
        )

    private fun applyCoupondb(total:Double){
            CoroutineScope(Dispatchers.Main).launch {
                 applyCouponEntity = AppDataBase.invoke(context)?.searchDetailsDao()?.getCouponDetails()
               // val coupon=  AppDataBase.invoke(context)?.searchDetailsDao()?.getCoupon()
                Log.d("couponDataEntity",applyCouponEntity?.coupon_id.toString())
                if (applyCouponEntity?.discount_amount!=null) {
                    if (total>= applyCouponEntity?.minimum_price!!) {
                        couponAmountValueTextView.text = "-" + "\u20B9" + applyCouponEntity?.discount_amount
                        couponCodeTextView.text="("+applyCouponEntity?.coupon_code+")"
                        priceTextView.text = "\u20B9" + applyCouponEntity?.discount_amount?.let { total.minus(it) }
                        paidPriceTextView.text = "\u20B9" + applyCouponEntity?.discount_amount?.let { it1 -> total.minus(it1) }
                    }else{
                        couponAmountValueTextView.text = "-" + "\u20B9" + 0
                        priceTextView.text = "\u20B9" + total
                        paidPriceTextView.text = "\u20B9" + total
                        couponCodeTextView.visibility=View.GONE
                        couponCodeRemoveTextView.visibility=View.GONE
                        applyCouponEntity?.discount_amount=0.0
                        applyCouponEntity?.coupon_id=0
                        applyCouponEntity?.total_discounted_amount=0.0
                    }

                }else{
                    couponAmountValueTextView.text ="\u20B9" + 0.0
                    couponAmountTextView.visibility =View.GONE
                    couponAmountValueTextView.visibility =View.GONE
                    priceTextView.text= "\u20B9"+ total
                    paidPriceTextView.text="\u20B9"+total
                    couponCodeTextView.visibility=View.GONE
                    couponCodeRemoveTextView.visibility=View.GONE


                }
            }

    }

    private fun couponDetails(total:Double) {
        if (viewModel.couponCode?.discount_amount!=null) {
            if (total>= viewModel.couponCode?.minimum_price!!) {
                couponAmountValueTextView.text = "-" + "\u20B9" + viewModel.couponCode?.discount_amount
                couponCodeTextView.text="("+viewModel.couponCode?.coupon_code+")"
                priceTextView.text = "\u20B9" + viewModel.couponCode?.discount_amount?.let { total.minus(it) }
                paidPriceTextView.text = "\u20B9" + viewModel.couponCode?.discount_amount?.let { it1 -> total.minus(it1) }
            }else{
                couponAmountValueTextView.text = "-" + "\u20B9" + 0
                priceTextView.text = "\u20B9" + total
                paidPriceTextView.text = "\u20B9" + total
                couponCodeTextView.visibility=View.GONE
                couponCodeRemoveTextView.visibility=View.GONE
                viewModel.couponCode?.discount_amount=0.0
                viewModel.couponCode?.coupon_id=0
                viewModel.couponCode?.total_discounted_amount=0.0
            }

        }else{
            couponAmountValueTextView.text ="\u20B9" + 0.0
            couponAmountTextView.visibility =View.GONE
            couponAmountValueTextView.visibility =View.GONE
            priceTextView.text= "\u20B9"+ total
            paidPriceTextView.text="\u20B9"+total
            couponCodeTextView.visibility=View.GONE
            couponCodeRemoveTextView.visibility=View.GONE


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
                    total=0.0

                    for ( i in 0 until  cartList.size){

                        total=total.plus(cartList[i].total_product_price)

                    }

                    itemPriceTextView.text="\u20B9"+total.toString()


                   // couponDetails(total)
                      applyCoupondb(total)

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
   private fun updateDeleteItemUi(state: ApiState){
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
        /*priceTextView.text= "\u20B9"+total
        paidPriceTextView.text="\u20B9"+total*/
       // couponDetails(total)
        applyCoupondb(total)
    }

    override fun removeOnItem(action: Int?, position: Int) {
        viewModel.getAddToCart("Bearer"+" "+sessionManagement?.getToken(),cartList[position].product_id,action)
        total= total.minus(cartList[position].get_product.sales_price)
        itemPriceTextView.text="\u20B9"+total
       /* priceTextView.text= "\u20B9"+total
        paidPriceTextView.text="\u20B9"+total*/
     //   couponDetails(total)
        applyCoupondb(total)


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
                  itemPriceTextView.text="\u20B9"+total
                 /* paidPriceTextView.text="\u20B9"+total
                  priceTextView.text= "\u20B9"+total*/
                 // couponDetails(total)
                  applyCoupondb(total)

                  //



              })

            alertDialogBuilder.setNegativeButton("No",DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })
        val alertDialog:AlertDialog = alertDialogBuilder.create();

        alertDialog.show()

    }


}