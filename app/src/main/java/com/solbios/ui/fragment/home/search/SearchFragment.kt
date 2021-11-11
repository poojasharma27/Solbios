package com.solbios.ui.fragment.home.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.firstapp.sharedPreference.SessionManagement
import com.solbios.databinding.FragmentSearchBinding
import com.solbios.db.AppDataBase
import com.solbios.db.entities.SearchData
import com.solbios.mapper.toSearchDataEntity
import com.solbios.model.search.Data
import com.solbios.model.search.SearchRoot
import com.solbios.model.search.TrendingProduct
import com.solbios.network.ApiState
import com.solbios.other.internetCheck
import com.solbios.ui.adapter.RecentSearchAdapter
import com.solbios.ui.adapter.SearchAdapter
import com.solbios.ui.adapter.TrendingAdapter
import com.solbios.ui.viewModel.home.searchViewModel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.layout_search_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchAdapter.OnSearchItemClickListener,TrendingAdapter.OnTrendingItemClickListener,RecentSearchAdapter.OnRecentSearchItem {

    private var binding: FragmentSearchBinding? = null
    private val viewModel: SearchViewModel by viewModels()
    var sessionManagement: SessionManagement? = null
    var adapter: RecentSearchAdapter? = null
   private val searchList = mutableListOf<Data>()
  private  val trendingProductsList = mutableListOf<TrendingProduct>()
  private  var searchDataEntity: List<SearchData?>? = null
   private var searchValue: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        binding?.viewModel = viewModel
        internetCheck(context)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManagement = context?.let { SessionManagement(it) }
        viewModel.getSearch("Bearer" + " " + sessionManagement?.getToken(), null)
        setToolbar()
        getRecentSearch()

    }

    private fun setToolbar() {
        trendingProductsTextView.visibility = View.VISIBLE

        backArrowImageView.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }

        searchViewEditText.addTextChangedListener {
            searchList.clear()
            viewModel.getSearch("Bearer" + " " + sessionManagement?.getToken(), it.toString())
            if (it?.length == 0) {
                recentSearchRelativeLayout.visibility = View.VISIBLE
                trendingProductsTextView.visibility = View.VISIBLE
                trendingProductsRecyclerView.visibility = View.VISIBLE
                searchRecyclerView.visibility = View.GONE

            } else {
                recentSearchRelativeLayout.visibility = View.GONE
                trendingProductsTextView.visibility = View.GONE
                trendingProductsRecyclerView.visibility = View.GONE
                searchRecyclerView.visibility = View.VISIBLE

            }
            if (searchValue!=null) {
                searchValue = it.toString()
            }else{
                searchValue=null
            }
        }
        startJob()
        clearAllTextView.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                AppDataBase.invoke(context)?.clearAllTables()
                recentSearchRecyclerView.adapter = null
                recentSearchRelativeLayout.visibility = View.INVISIBLE

            }
        }


    }

   private lateinit var searchJob: Job

    private fun startJob() {
        searchJob = lifecycleScope.launch {
            viewModel.apiState.collect {
                updateUi(it)
            }
        }
        searchJob.start()
    }

    private fun updateUi(state: ApiState) {
        when (state) {
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {
                Log.d("TAG", "updateUi: ")
            }
            ApiState.Loading -> {
                Log.d("TAG", "updateUi: ")
            }
            is ApiState.Success<*> -> {
                (state.data as? SearchRoot)?.let {
                    searchList.clear()
                    searchList.addAll(it.data)
                    trendingProductsList.clear()
                    trendingProductsList.addAll(it.trending_product)
                    setAdapter(searchList)
                    setTrendingList(trendingProductsList)


                }


            }

        }
    }

    private fun setTrendingList(trendingProductsList: List<TrendingProduct>) {
        val adapter = TrendingAdapter(trendingProductsList,this)
        trendingProductsRecyclerView.adapter = adapter
    }

    private fun setAdapter(searchList: List<Data>) {
        val adapter = SearchAdapter(searchList, this)
        searchRecyclerView.adapter = adapter
    }

    override fun searchItemClickListener(view: View, position: Int) {
        searchDb(position)
        Navigation.findNavController(view).navigate(
            SearchFragmentDirections.actionSearchFragmentToProductListDescription(searchList[position].id)
        )
    }

    // list Add in Db code
    private fun updateDb() {
        CoroutineScope(Dispatchers.Main).launch {

            val searchEntityList = mutableListOf<SearchData>()
            for (item in searchList) {
                searchEntityList.add(item.toSearchDataEntity())
            }
            context?.let {
                val searchDataEntity = AppDataBase.invoke(it)?.searchDetailsDao()
                    ?.addDetails(searchEntityList)

                Log.d("articleEntity", searchDataEntity.toString())
            }

        }
    }

    private fun searchDb(position: Int) {
            val searchEntity = SearchData(
                searchList[position].id,
                searchList[position].image,
                searchList[position].sub_title,
                searchList[position].title
            )

            CoroutineScope(Dispatchers.Main).launch {
                context?.let {
                    val searchDataEntity = AppDataBase.invoke(it)?.searchDetailsDao()
                        ?.searchAddDetails(searchEntity)

                    Log.d("articleEntity", searchDataEntity.toString())
                }
            }

    }

    private fun getRecentSearch() {
        CoroutineScope(Dispatchers.Main).launch {
            searchDataEntity = AppDataBase.invoke(context)?.searchDetailsDao()?.getAllRecentSearch()
            setSearchAdapter(searchDataEntity)
            setRecentText(searchDataEntity)
         //   setSearchValue(searchValue)
        }
    }


    private fun setRecentText(searchDataEntity: List<SearchData?>?) {
        if (searchDataEntity?.size != 0  && searchValue==null) {
            recentSearchRelativeLayout.visibility = View.VISIBLE
        } else {
            recentSearchRelativeLayout.visibility = View.INVISIBLE

        }
    }

    private fun setSearchAdapter(searchDataEntity: List<SearchData?>?) {
        adapter = RecentSearchAdapter(searchDataEntity as List<SearchData>, this)
        recentSearchRecyclerView.adapter = adapter
    }

    override fun onRecentSearch(view: View, position: Int) {
        val id = searchDataEntity?.get(position)?.id
        Navigation.findNavController(view)
            .navigate(SearchFragmentDirections.actionSearchFragmentToProductListDescription(id!!))

    }

    override fun trendingItemClickListener(view: View, position: Int) {
        Navigation.findNavController(view).navigate(SearchFragmentDirections.actionSearchFragmentToProductListDescription(trendingProductsList[position].id))
    }


}