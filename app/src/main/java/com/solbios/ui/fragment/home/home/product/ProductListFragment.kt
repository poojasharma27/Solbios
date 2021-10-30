package com.solbios.ui.fragment.home.home.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.solbios.R
import com.solbios.model.productList.Data
import com.solbios.model.productList.ProductionList
import com.solbios.network.ApiState
import com.solbios.ui.adapter.ProductListAdapter
import com.solbios.ui.dialog.FilterBottomSheetDialogFragment
import com.solbios.ui.dialog.ShortBottomSheetDialogFragment
import com.solbios.ui.viewModel.home.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_product_list.*
import kotlinx.android.synthetic.main.layout_toolbar_with_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.RecyclerView
import com.firstapp.sharedPreference.SessionManagement
import com.solbios.databinding.FragmentProductListBinding
import com.solbios.model.addtocart.AddToCartRoot
import com.solbios.model.filter.Brand
import com.solbios.model.filter.Category
import kotlinx.android.synthetic.main.layout_toolbar_with_search.cartImageView
import kotlinx.android.synthetic.main.layout_toolbar_with_search.locationTextView
import kotlin.collections.ArrayList


@AndroidEntryPoint
class ProductListFragment : Fragment(),ProductListAdapter.ItemClickListener {

 var binding: FragmentProductListBinding?= null
  private val viewModel:ProductViewModel by viewModels()
    val productList = ArrayList<Data>()
    private var loading = false
    var pastVisiblesItems = 0
    var visibleItemCount:Int = 0
    var totalItemCount:Int = 0
    lateinit var layoutManager :LinearLayoutManager
   var sessionManagement: SessionManagement?=null
   var cartTotalPrice:Double=0.0
    var page:Int=1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding= FragmentProductListBinding.inflate(layoutInflater)
        binding.viewModel=viewModel
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManagement= context?.let { SessionManagement(it) }
        toolbar()
        startJob()
        locationTextView.visibility=View.INVISIBLE
        viewModel.getProductList("Bearer"+" "+sessionManagement?.getToken(),page)
        sortByOnClickListener()
        filterOnClickListener()
  startFilterJob()
        viewModel.getFilter()
        startAddToCartJob()


    }
    private fun addPagination(){
        binding?.productListRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy >= 0&& !loading) { //check for scroll down
                    visibleItemCount = layoutManager.childCount
                    totalItemCount = layoutManager.itemCount
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()

                    if (visibleItemCount + pastVisiblesItems >= totalItemCount && pastVisiblesItems >= 0  ) {
                        Log.e("pagination", "Last Item Wow !")
                        // Do pagination.. i.e. fetch new data
                        loading = true
                        page++
                        viewModel.getProductList("Bearer"+" "+sessionManagement?.getToken(),page)

                    }
                    else{
                        Log.e("pagination",(visibleItemCount + pastVisiblesItems).toString())
                    }

                }
            }
        })
    }


    private fun pagination(next:Int?) {
        if (next?.let { it > 1 } == true) {
            loading=false
            addPagination()
        } else {
            loading=true
        }
    }


    private fun toolbar() {
        backImageView.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }
        searchImageView.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_productListFragment_to_searchFragment)
        }
        cartImageView.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_productListFragment_to_cartFragment)
        }
        goToCartTextView.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_productListFragment_to_cartFragment)

        }
    }

    companion object {
         val categoryList = ArrayList<Category>()
         val brandList = ArrayList<Brand>()
    }


  var filterBottomSheet : FilterBottomSheetDialogFragment? = null
    private fun filterOnClickListener() {
        filterBottomSheet = FilterBottomSheetDialogFragment(object : FilterBottomSheetDialogFragment.CallbackListener{
            override fun onBrandsSelected(brandIdList: String) {
                viewModel.brandId=brandIdList
                viewModel.getProductList("Bearer"+" "+sessionManagement?.getToken(),page)            }

            override fun onCategorySelected(catIdList: Int) {
                viewModel.data=catIdList.toString()
                viewModel.getProductList("Bearer"+" "+sessionManagement?.getToken(),page)            }

        })

         filterTextView.setOnClickListener {
             filterBottomSheet.apply {
                 filterBottomSheet?.show(childFragmentManager,filterBottomSheet?.tag)

             }
         }
    }
    var shortByBottomSheet : ShortBottomSheetDialogFragment? = null


    private fun sortByOnClickListener() {
        sortTextView.setOnClickListener {
            shortByBottomSheet= ShortBottomSheetDialogFragment(object : ShortBottomSheetDialogFragment.SortListener{
                override fun onLowToHigh(view: View, descending: String) {
                    viewModel.asc=descending
                    viewModel.getProductList("Bearer"+" "+sessionManagement?.getToken(),page)

                }

                override fun onHighToLow(view: View, ascending: String) {
                    viewModel.asc=ascending
                    viewModel.getProductList("Bearer"+" "+sessionManagement?.getToken(),page)
                }

            })
            shortByBottomSheet.apply {
                shortByBottomSheet?.show(childFragmentManager,shortByBottomSheet?.tag)
            }

        }
    }

    lateinit var productJob : Job
    fun updateUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {

            }
            ApiState.Loading -> {

            }
            is ApiState.Success<*> -> {

                (state.data as? ProductionList)?.let {
                    productList.clear()
                    productList.addAll(it.data)
                   setProductList(productList)
                    allProductDeatilsTextView.text=it.total.toString()+" "+"products available "
                   pagination(it.next_page)
                    setBadge(it.cart_total)
                   setItem(it.cart_total,it.cart_total_amount.toDouble())
                 cartTotalPrice=it.cart_total_amount.toDouble()
                }


            }

        }
    }

    private fun setItem(cartTotal: Int,total:Double) {
     if (cartTotal==0){
         relativeLayout.visibility=View.INVISIBLE
     }else{
       itemSizeTextView.text=cartTotal.toString()+" "+"Item"
         salePriceTextView.text="\u20b9"+total.toString()
         relativeLayout.visibility=View.VISIBLE
     }
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
                   setItem(it.data.cart_total,cartTotalPrice)

                }

            }

        }
    }

    private fun setBadge(cartTotal: Int) {
        if (cartTotal==0){
            cart_badgeTextView.visibility=View.GONE
        }else{
            cart_badgeTextView.visibility=View.VISIBLE
            cart_badgeTextView.text=cartTotal.toString()
        }
    }


    private fun startJob() {
        productJob =  lifecycleScope.launch {
            viewModel.apiState.collect {
                updateUi(it)

            }
        }

        productJob.start()
    }

    private fun startAddToCartJob() {
        addtoCartJob =  lifecycleScope.launch {
            viewModel.apiStateAddToCart.collect {
                updateAddToCartUi(it)

            }
        }

        addtoCartJob.start()
    }

    override fun onResume() {
        super.onResume()
       // viewModel.getProductList("Bearer"+" "+sessionManagement?.getToken())
    }
   fun setProductList(list:List<Data>){
       val adapter=ProductListAdapter(list,this)
       productListRecyclerView.adapter=adapter
       layoutManager=LinearLayoutManager(context)
       productListRecyclerView.layoutManager=layoutManager

   }



    override fun addOnItem(action: Int?, position: Int) {
        cartTotalPrice=cartTotalPrice.plus(productList[position].sales_price)
        viewModel.getAddToCart("Bearer"+" "+sessionManagement?.getToken(),productList[position].id,action)
    }

    override fun removeOnItem(action: Int?, position: Int) {
        startAddToCartJob()
        cartTotalPrice=cartTotalPrice.minus(productList[position].sales_price)
        viewModel.getAddToCart("Bearer"+" "+sessionManagement?.getToken(),productList[position].id,action)

    }

    private  lateinit var filterJob : Job
    fun updateFilterUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {
                Log.d("TAG", "updateUi: ")
            }
            ApiState.Loading -> {
                Log.d("TAG", "updateUi: ")
            }
            is ApiState.Success<*> -> {
                (state.data as? com.solbios.model.filter.Data)?.let {
                    categoryList.clear()
                    brandList.clear()
                    categoryList.addAll(it.categories)
                    brandList.addAll(it.brands)
                }


            }

        }
    }
    private fun startFilterJob() {
        filterJob =  lifecycleScope.launch {
            viewModel.apiStateFilter.collect {
                updateFilterUi(it)

            }
        }

        filterJob.start()
    }
}