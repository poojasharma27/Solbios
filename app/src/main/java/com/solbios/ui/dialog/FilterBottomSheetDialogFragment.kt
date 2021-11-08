package com.solbios.ui.dialog

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.solbios.databinding.LayoutFilterBottomSheetBinding
import com.solbios.interfaces.BrandClickListener
import com.solbios.interfaces.ItemClickListener
import com.solbios.model.filter.Brand
import com.solbios.model.filter.Category
import com.solbios.network.ApiState
import com.solbios.ui.adapter.FilterBrandAdapter
import com.solbios.ui.adapter.FilterCategoryAdapter
import com.solbios.ui.fragment.home.home.product.ProductListFragment
import com.solbios.ui.viewModel.home.FilterBottomSheetDialogViewModel
import com.solbios.ui.viewModel.home.FilterCallbackListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_filter_bottom_sheet.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterBottomSheetDialogFragment(private  var callBacklistener: CallbackListener) : BottomSheetDialogFragment(),ItemClickListener,BrandClickListener {

    private var binding: LayoutFilterBottomSheetBinding?=null
    private  val viewModel:FilterBottomSheetDialogViewModel by viewModels()

    //var catId=ArrayList<Int>()
    var catId:Int?=null
        //var brandIdList= ArrayList<Int>()
    var brandIdList:Int?=null
     var pos:Int?=null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= LayoutFilterBottomSheetBinding.inflate(layoutInflater)
        binding?.viewModel=viewModel
        return binding?.root

    }
    var listener : FilterCallbackListener? = null
    fun setBrandsListener(listener : FilterCallbackListener){
        this.listener = listener
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crossOnClickListener()
      /*  if(ProductListFragment.categoryList.isEmpty()&&ProductListFragment.brandList.isEmpty()) {
            viewModel.getFilter()
        }else{
           setCategory(ProductListFragment.categoryList)
            setBrand(ProductListFragment.brandList)
        }*/
        setSelected()
        //startJob()
        setCategory(ProductListFragment.categoryList)
        setBrand(ProductListFragment.brandList)

      applyTextView.setOnClickListener {
       val brandId=brandIdList
          val id = catId
          callBacklistener?.onBrandsSelected(brandId.toString(),Integer.parseInt(id.toString()))
          callBacklistener?.onCategorySelected(Integer.parseInt(id.toString()),brandId.toString())
       /*  if(catId==null){
            val brandId=brandIdList
            callBacklistener?.onBrandsSelected(brandId.toString())
         }else {
             val id = catId
             callBacklistener?.onCategorySelected(Integer.parseInt(id.toString()))

        } */
        dialog?.dismiss()
     }
    clearTextView.setOnClickListener {
      //  brandIdList.clear()
        pos?.let {
            ProductListFragment.brandList[it].selected =false
        }

        dialog?.dismiss()
    }
    }

    private fun setSelected() {
        viewModel.category.observe(viewLifecycleOwner, Observer {
            categoryRecyclerView.visibility=View.VISIBLE
            brandRecyclerView.visibility=View.GONE
            categoryTextView.setTextColor(Color.parseColor("#0096FF"))
            brandTextView.setTextColor(Color.parseColor("#dfdfdf"))

        })
        viewModel.brand.observe(viewLifecycleOwner, Observer {
            categoryRecyclerView.visibility=View.GONE
            brandRecyclerView.visibility=View.VISIBLE
            brandTextView.setTextColor(Color.parseColor("#0096FF"))
            categoryTextView.setTextColor(Color.parseColor("#dfdfdf"))

        })    }

    private  lateinit var filterJob : Job
    fun updateUi(state: ApiState){
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
                    ProductListFragment.categoryList.clear()
                    ProductListFragment.brandList.clear()
                    ProductListFragment.categoryList.addAll(it.categories)
                    ProductListFragment.brandList.addAll(it.brands)
                   setCategory(ProductListFragment.categoryList)
                    setBrand(ProductListFragment.brandList)
                }


            }

        }
    }

    private fun setBrand(brandList: List<Brand>) {
        val adapter=FilterBrandAdapter(brandList,this)
        brandRecyclerView.adapter=adapter

    }

    private fun setCategory(categoryList: List<Category>) {
        val adapter=FilterCategoryAdapter(categoryList,this)
        categoryRecyclerView.adapter=adapter
    }

    private fun startJob() {
        filterJob =  lifecycleScope.launch {
            viewModel.apiState.collect {
                updateUi(it)

            }
        }

        filterJob.start()
    }

    private fun crossOnClickListener() {
        clearImageView.setOnClickListener {
            dialog?.dismiss()
        }
    }

    override fun onViewClicked(view: View, position: Int) {
        catId=ProductListFragment.categoryList[position].id
    //  catId.add( ProductListFragment.categoryList[position].id)
        ProductListFragment.categoryList[position].selected = !ProductListFragment.categoryList[position].selected

    }

    override fun onBrandViewClicked(view: View, position: Int) {
        pos=position
        brandIdList=ProductListFragment.brandList[position].id
       // brandIdList .add (ProductListFragment.brandList[position].id)
        ProductListFragment.brandList[position].selected = !ProductListFragment.brandList[position].selected
        Log.e("TAG", "onBrandViewClicked:$brandIdList ",)

    }


    private  lateinit var productJob : Job


   /* private fun startProductList( catId:String?, brandIdList:String?,page:Int?){
    productJob=lifecycleScope.launch {
         viewModel.productListRepository.getProductionList(catId,brandIdList,page).onStart {
             viewModel._apiStateProduct.value=ApiState.Loading

         }.catch { e->
             viewModel._apiStateProduct.value=ApiState.Failure(e)


         }.collect {
             viewModel._apiStateProduct.value=ApiState.Success(it.data)
             productList(it.data)
             dialog?.dismiss()

         }
    }
    }
    fun productList(list:List<Data>){
        val adapter=ProductListAdapter(list)
        productListRecyclerView.adapter=adapter

    }

*/


    interface CallbackListener {
        fun onBrandsSelected(brandIdList: String,catIdList: Int)
        fun onCategorySelected(catIdList: Int,brandIdList: String)
    }


}