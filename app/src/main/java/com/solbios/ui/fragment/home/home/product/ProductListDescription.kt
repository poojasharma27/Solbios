package com.solbios.ui.fragment.home.home.product

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.solbios.R
import com.solbios.model.productDetails.Data
import com.solbios.model.productDetails.PacketSize
import com.solbios.model.productDetails.SimilarProduct
import com.solbios.network.ApiState
import com.solbios.ui.viewModel.home.ProductListDescriptionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_product_list_description.*
import kotlinx.android.synthetic.main.layout_toolbar_with_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import android.view.ViewTreeObserver.OnScrollChangedListener
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.firstapp.sharedPreference.SessionManagement
import com.solbios.databinding.FragmentProductListDescriptionBinding
import com.solbios.model.addtocart.AddToCartRoot
import kotlinx.android.synthetic.main.fragment_product_list_description.actualPriceTextView
import kotlinx.android.synthetic.main.fragment_product_list_description.addCartTextView
import kotlinx.android.synthetic.main.fragment_product_list_description.countTextView
import kotlinx.android.synthetic.main.fragment_product_list_description.offerPriceTextView
import kotlinx.android.synthetic.main.fragment_product_list_description.percentOffTextView
import kotlinx.android.synthetic.main.fragment_product_list_description.plusImageView
import kotlinx.android.synthetic.main.fragment_product_list_description.plusMinusLinearLayout
import kotlinx.android.synthetic.main.fragment_product_list_description.productImageView
import kotlinx.android.synthetic.main.item_product_list.*


@AndroidEntryPoint
class ProductListDescription : Fragment(),PacketSizeAdapter.PacketSizeListener {

    private  var binding: FragmentProductListDescriptionBinding?=null
    private  val viewModel:ProductListDescriptionViewModel by viewModels()
    private val similarProductList=ArrayList<SimilarProduct>()
    private val packetSize=ArrayList<PacketSize>()
    private val args:ProductListDescriptionArgs by navArgs()
  var sessionManagement:SessionManagement?=null
    var id:Int?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentProductListDescriptionBinding.inflate(layoutInflater)
        binding?.viewModel=viewModel
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManagement= context?.let { SessionManagement(it) }
        toolbar(view)
        viewModel.getDetails("Bearer"+" "+sessionManagement?.getToken(),args.id)
        startJob()
        addToCartOnClickListener()
        locationTextView.visibility=View.INVISIBLE
        scrollViewChangeListener()
       startAddToCartJob()

    }

    private fun toolbar(view: View) {
        val backImageView=view.findViewById<ImageView>(R.id.backImageView)
        backImageView.setOnClickListener {
          Navigation.findNavController(it).popBackStack()
        }
        val searchImageView :ImageView=searchImageView
        searchImageView.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_productListDescription_to_searchFragment)
        }
        cartImageView.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_productListDescription_to_cartFragment)
        }

    }

    private fun scrollViewChangeListener(){
        scrollView.viewTreeObserver.addOnScrollChangedListener(OnScrollChangedListener {
            if (scrollView != null) {
                if (scrollView.getChildAt(0)
                        .getBottom() <= scrollView.getHeight() + scrollView.getScrollY()
                ) {
                    relativeLayout.setVisibility(View.VISIBLE)
                } else {
                    relativeLayout.setVisibility(View.INVISIBLE)
                }
            }
        })
    }



    lateinit var productDetailsJob : Job
    fun updateUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {

            }
            ApiState.Loading -> {

            }
            is ApiState.Success<*> -> {

                (state.data as? Data)?.let {
                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(22))
             activity?.let { it1 -> Glide.with(it1).load(it.detail.image).apply(requestOptions).into(productImageView) }
                    productTitleTextView.text=it.detail.title
                    unitTextView.text=it.detail.pack_size
                    desTextView.text=it.detail.description
                   id=it.detail.id
                    setSalePrices(it.detail.sales_price)
                setActualPrices(it.detail.price)
             setPercentAge(it.detail.price,it.detail.sales_price)

                    similarProductList.clear()
                    similarProductList.addAll(it.similar_products)
                    packetSize.clear()
                    packetSize.addAll(it.packet_sizes)
                    setSimilarProduct(similarProductList)
                    setPacketSize(packetSize)
                     if (it.detail.cart_product!=null){
                         addCartTextView.visibility=View.INVISIBLE
                         plusMinusLinearLayout.visibility=View.VISIBLE
                         countTextView.text=it.detail.cart_product.quantity.toString()
                         addItemTextView.text="Go to Cart"

                     }else{
                         addItemTextView.text="Add to Cart"

                     }
              setBadge(it.cart_total)
                }

            }

        }
    }

    private fun setSalePrices(salesPrice: Float) {
        val offerPrice=  String.format("%.0f",salesPrice)
        offerPriceTextView.text="\u20B9"+ offerPrice
        salePriceTextView.text="\u20B9"+ offerPrice
    }

    private fun setActualPrices(price: Float) {
        val rightPrice=  String.format("%.0f",price)
        actualPriceTextView.text="\u20B9"+ rightPrice
        acqratePriceTextView.text="\u20B9"+ rightPrice
       actualPriceTextView.paintFlags= actualPriceTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        acqratePriceTextView.paintFlags= actualPriceTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

    }

    private fun setPercentAge(price: Float, salesPrice: Float) {
        if (price==salesPrice) {
            percentOffTextView.visibility=View.INVISIBLE
            actualPriceTextView.visibility=View.INVISIBLE
        }else{
            val percentage=((price?.minus(salesPrice))?.times(100))/price
            val rounded = String.format("%.1f", percentage)
            percentOffTextView.text= "$rounded% off"
            perOffTextView.text="$rounded% off"
        }
    }


    private fun setSimilarProduct(similarProductList: ArrayList<SimilarProduct>) {
        val adapter=SimilarProductAdapter(similarProductList)
        similarRecyclerView.adapter=adapter

    }
  private fun setPacketSize(similarProductList: ArrayList<PacketSize>) {
        val adapter=PacketSizeAdapter(similarProductList,this)
        packetSizeRecyclerView.adapter=adapter

    }


    private fun startJob() {
        productDetailsJob = lifecycleScope.launch {
            viewModel.apiState.collect {
                updateUi(it)
            }
        }
        productDetailsJob.start()
    }
    lateinit var addtoCartJob:Job
    fun updateAddToCartUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {

            }
            ApiState.Loading -> {

            }
            is ApiState.Success<*> -> {
                (state.data as? AddToCartRoot)?.let {
                    setBadge(it.data.cart_total)

                }

            }

        }
    }

    private fun startAddToCartJob() {
        addtoCartJob =  lifecycleScope.launch {
            viewModel.apiStateAddToCart.collect {
                updateAddToCartUi(it)

            }
        }

        addtoCartJob.start()
    }

    private fun setBadge(cartTotal: Int) {
        if (cartTotal==0){
            cart_badgeTextView.visibility=View.GONE
        }else{
            cart_badgeTextView.visibility=View.VISIBLE
            cart_badgeTextView.text=cartTotal.toString()
        }
    }

    fun addToCartOnClickListener(){
        addCartTextView.setOnClickListener {
            addCartTextView.visibility=View.INVISIBLE
            plusMinusLinearLayout.visibility=View.VISIBLE
             addItemTextView.text="Go to Cart"
                countTextView.text = "1"

            viewModel.getAddToCart("Bearer"+" "+sessionManagement?.getToken(),id,1)

        }
        plusImageView.setOnClickListener {
            var count=  Integer. parseInt(countTextView.text.toString())
            count++
                countTextView.text=count.toString()
          val  productid=id
            viewModel.getAddToCart("Bearer"+" "+sessionManagement?.getToken(),productid,1)
        }
        minusImageViewDes.setOnClickListener {
            var count = Integer.parseInt(countTextView.text.toString())
            count--
            countTextView.text=count.toString()
            if (count == 0) {
                addCartTextView.visibility = View.VISIBLE
                plusMinusLinearLayout.visibility = View.GONE
                addItemTextView.text="Add to Cart"
            }
            viewModel.getAddToCart("Bearer"+" "+sessionManagement?.getToken(),id,2)


        }
    }

    override fun onClickPacketSize(position: Int) {
        viewModel.getDetails("Bearer"+" "+sessionManagement?.getToken(),packetSize[position].id)
        /*val action = ProductListDescriptionDirections.actionProductListDescriptionToProductListDescription(packetSize[position].id)
        view?.findNavController()?.navigate(action)*/
    }
}