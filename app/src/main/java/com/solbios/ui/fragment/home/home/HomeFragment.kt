package com.solbios.ui.fragment.home.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.adapter.HomeBannerAdapter
import com.firstapp.sharedPreference.SessionManagement
import com.solbios.R
import com.solbios.databinding.FragmentHomeBinding
import com.solbios.interfaces.BrandClickListener
import com.solbios.interfaces.ItemClickListener
import com.solbios.model.home.*
import com.solbios.network.ApiState
import com.solbios.ui.adapter.FeatureBrandAdapter
import com.solbios.ui.adapter.PopularCategoriesAdapter
import com.solbios.ui.viewModel.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() ,ItemClickListener ,BrandClickListener{

    var binding: FragmentHomeBinding?=null
   private val popularCategoriesList = mutableListOf<Category>()
   private val featureBrandsArrayList = mutableListOf<Brand>()
   private val  bannerList = mutableListOf<Banner>()
   private  val viewModel: HomeViewModel by viewModels()
    var sessionManagement: SessionManagement?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomeBinding.inflate(layoutInflater)
        binding?.viewModel=viewModel
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManagement= context?.let { SessionManagement(it) }
        viewModel.getHomeDetails("Bearer"+" "+sessionManagement?.getToken())
        startJob()

    }


  private  lateinit var homeJob : Job
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
                (state.data as? PopularCategoriesRoot)?.let {
                    popularCategoriesList.clear()
                    popularCategoriesList.addAll(it.data.categories)
                    featureBrandsArrayList.clear()
                    featureBrandsArrayList.addAll(it.data.brands)
                    bannerList.clear()
                    bannerList.addAll(it.data.banners)
                    setPopularCategoriesAdapter(popularCategoriesList)
                    setFeatureBrandsAdapter(featureBrandsArrayList)
                    setBannerList(bannerList)
                    setBadge(it.cart_total)
                    setToolbar()

                }


            }

        }
    }

    private fun setToolbar() {
        cartImageView.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_cartFragment)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setBadge(cartTotal: Int) {
        if (cartTotal==0){
            cart_badge.visibility=View.GONE
        }else{
            cart_badge.visibility=View.VISIBLE
            cart_badge.text=cartTotal.toString()
        }

    }

    private fun startJob() {
        homeJob =  lifecycleScope.launch {
            viewModel.apiState.collect {
                updateUi(it)

            }
        }

        homeJob.start()
    }


    private fun setFeatureBrandsAdapter(list:List<Brand>) {
        val adapter=FeatureBrandAdapter(list,this)
       binding?.brandsRecyclerView?.adapter=adapter
    }


    private fun setPopularCategoriesAdapter(list:List<Category>) {
        val adapter=PopularCategoriesAdapter(list,this)
        binding?.popularCategoriesRecyclerView?.adapter=adapter
    }




   private fun setBannerList(list:List<Banner>) {
     val  bannerAdapter = context?.let { HomeBannerAdapter(list, it) }
       val linearLayoutManager = LinearLayoutManager(context)
       linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
       binding?.pager?.adapter = bannerAdapter
      binding?. indicator?.setViewPager(pager)

   }

    override fun onViewClicked(view: View, position: Int) {
        val action= HomeFragmentDirections.actionHomeFragmentToProductListFragment( "",popularCategoriesList[position].id.toString())
       view.findNavController().navigate(action)
    }

    override fun onBrandViewClicked(view: View, position: Int) {
        val action= HomeFragmentDirections.actionHomeFragmentToProductListFragment( featureBrandsArrayList[position].id.toString(),"")
        view.findNavController().navigate(action)
    }


}