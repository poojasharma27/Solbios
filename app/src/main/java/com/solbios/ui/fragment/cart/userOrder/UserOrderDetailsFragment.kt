package com.solbios.ui.fragment.cart.userOrder

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
import androidx.recyclerview.widget.RecyclerView
import com.firstapp.sharedPreference.SessionManagement
import com.solbios.R
import com.solbios.databinding.FragmentUserOrderDetailsBinding
import com.solbios.model.userOrder.Data
import com.solbios.model.userOrder.UserOrderRoot
import com.solbios.network.ApiState
import com.solbios.other.Constants
import com.solbios.other.internetCheck
import com.solbios.other.isNetworkAvailable
import com.solbios.other.toast
import com.solbios.ui.adapter.UserOrderAdapter
import com.solbios.ui.viewModel.home.UserOrderDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_product_list.*
import kotlinx.android.synthetic.main.fragment_user_order_details.*
import kotlinx.android.synthetic.main.layout_toolbar_name.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserOrderDetailsFragment : Fragment(),UserOrderAdapter.ItemClickListener {

    private var binding:FragmentUserOrderDetailsBinding?=null
    private val viewModel:UserOrderDetailsViewModel by viewModels()
    var userOrderDetailsList= mutableListOf<Data>()
    var sessionManagement: SessionManagement?=null
    private var loading = false
    var pastVisiblesItems = 0
    var visibleItemCount:Int = 0
    var totalItemCount:Int = 0
    lateinit var layoutManager : LinearLayoutManager
      var page:Int=1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentUserOrderDetailsBinding.inflate(layoutInflater)
        binding?.viewModel=viewModel
        internetCheck(context)

        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManagement= context?.let { SessionManagement(it) }
        getUserOrder()
        setToolbar()
        startJob()

    }
    fun getUserOrder(){
        if( context?.let{ isNetworkAvailable(it) }==true) {
            viewModel.getUserOrder("Bearer"+" "+sessionManagement?.getToken(),page)

        }
        else{
            toast(context)
        }

        }

    private fun setToolbar() {
        locationTextView.text=Constants.myOrder
        backImageView.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_userOrderDetails_to_profileFragment)
        }

    }


    lateinit var userOrderDetailsJob : Job
    fun updateUi(state: ApiState){
        when(state){
            ApiState.Empty -> {

            }
            is ApiState.Failure -> {

            }
            ApiState.Loading -> {

            }
            is ApiState.Success<*> -> {
                (state.data as? UserOrderRoot)?.let {
                    userOrderDetailsList.addAll(it.data)
                    setAdapter(userOrderDetailsList)
                   val next=it.next_page
                    pagination(next)

                }


            }
        }
    }

    private fun setAdapter(userOrderDetailsList: List<Data>) {
      val adapter=UserOrderAdapter(userOrderDetailsList,this)
        if(adapter!=null) {
            yourOrderRecyclerView.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            yourOrderRecyclerView.layoutManager = layoutManager
        }
    }

    private fun addPagination(){
        binding?.yourOrderRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                            viewModel.getUserOrder("Bearer"+" "+sessionManagement?.getToken(),page)

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

        private fun startJob() {
        userOrderDetailsJob =  lifecycleScope.launch {
            viewModel.apiState.collect {
                updateUi(it)
            }



        }

        userOrderDetailsJob.start()
    }

    override fun OnItemClick(view: View, position: Int) {
        Navigation.findNavController(view).navigate(UserOrderDetailsFragmentDirections.actionUserOrderDetailsToOrderDetailsFragment(userOrderDetailsList[position].id.toString()))
    }
}
