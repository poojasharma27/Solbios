package com.solbios.ui.fragment.home.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.firstapp.sharedPreference.SessionManagement
import com.solbios.R
import com.solbios.databinding.FragmentSearchBinding
import com.solbios.db.AppDataBase
import com.solbios.db.entities.SearchData
import com.solbios.mapper.toSearchDataEntity
import com.solbios.model.home.PopularCategoriesRoot
import com.solbios.model.search.Data
import com.solbios.model.search.SearchRoot
import com.solbios.network.ApiState
import com.solbios.ui.adapter.RecentSearchAdapter
import com.solbios.ui.adapter.SearchAdapter
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
class SearchFragment : Fragment(),SearchAdapter.OnSearchItemClickListener {

  private var binding:FragmentSearchBinding?=null
  private val viewModel:SearchViewModel by viewModels()
    var sessionManagement: SessionManagement?=null
    var adapter:RecentSearchAdapter?=null

  val searchList= mutableListOf<Data>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding=FragmentSearchBinding.inflate(layoutInflater)
        binding?.viewModel=viewModel
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManagement= context?.let { SessionManagement(it) }
        setToolbar()
        recentSearchRelativeLayout.visibility=View.VISIBLE


    }

    private fun setToolbar() {
        backArrowImageView.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }

        searchViewEditText.addTextChangedListener {
            searchList.clear()
            viewModel.getSearch("Bearer"+" "+sessionManagement?.getToken(),it.toString())
            recentSearchRelativeLayout.visibility=View.INVISIBLE
        }
        startJob()
        getRecentSearch()
        clearAllTextView.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                AppDataBase.invoke(context)?.clearAllTables()
                adapter?.notifyDataSetChanged()

            }
        }
    }

    lateinit var searchJob: Job

    private fun startJob(){
        searchJob=lifecycleScope.launch {
            viewModel.apiState.collect {
                updateUi(it)
            }
        }
        searchJob.start()
    }

    private fun updateUi(state: ApiState) {
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
                (state.data as? SearchRoot)?.let {
                    searchList.clear()
                    searchList.addAll(it.data)
                setAdapter(searchList)

                }


            }

        }
    }

    private fun setAdapter(searchList: List<Data>) {
               val adapter=SearchAdapter(searchList,this)
        searchRecyclerView.adapter=adapter
    }

    override fun searchItemClickListener(view: View, position: Int) {
        searchDb(position)
        Navigation.findNavController(view).navigate(SearchFragmentDirections.actionSearchFragmentToProductListDescription(searchList[position].id))
    }
  // list Add in Db code
    private fun updateDb(){
        CoroutineScope(Dispatchers.Main).launch {

            val searchEntityList = mutableListOf<SearchData>()
            for (item in searchList) {
                searchEntityList.add(item.toSearchDataEntity())
            }
            context?.let {
                val SearchDataEntity = AppDataBase.invoke(it)?.searchDetailsDao()
                    ?.addDetails(searchEntityList)

                Log.d("articleEntity", SearchDataEntity.toString())
            }

        }
    }

    private fun searchDb(position:Int){
        val searchEntity=SearchData(searchList[position].id,searchList[position].image,searchList[position].sub_title,searchList[position].title)
        CoroutineScope(Dispatchers.Main).launch {
            context?.let {
                val searchDataEntity = AppDataBase.invoke(it)?.searchDetailsDao()
                    ?.searchAddDetails(searchEntity)

                Log.d("articleEntity", searchDataEntity.toString())
            }
        }
    }

    private fun getRecentSearch(){
        CoroutineScope(Dispatchers.Main).launch {
                val searchDataEntity = AppDataBase.invoke(context)?.searchDetailsDao()
                    ?.getAllRecentSearch()
                  adapter=RecentSearchAdapter(searchDataEntity as List<SearchData>)
                 recentSearchRecyclerView.adapter=adapter
                Log.d("articleEntity", searchDataEntity.toString())

        }
    }


}