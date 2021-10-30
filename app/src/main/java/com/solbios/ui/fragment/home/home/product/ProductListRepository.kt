package com.solbios.ui.fragment.home.home.product

import com.solbios.interfaces.ApiServiceIn
import com.solbios.model.addtocart.AddToCartRoot
import com.solbios.model.filter.FilterRoot
import com.solbios.model.productList.ProductionList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProductListRepository @Inject constructor (val apiServiceIn: ApiServiceIn) {

    suspend fun getProductionList(header: String?,categoryId:String?,brandId:String?,asc:String?,page:Int?)= flow<ProductionList>{
        emit(apiServiceIn.productList(header,categoryId,brandId,asc,page))

    }.flowOn(Dispatchers.IO)

    suspend fun getAddToCart(header:String?,productId:Int?,action:Int?)= flow<AddToCartRoot>{
        emit(apiServiceIn.addToCart(header,productId,action))

    }.flowOn(Dispatchers.IO)

    suspend fun getFilter()=flow<FilterRoot>{
        emit(apiServiceIn.filterList())
    }.flowOn(Dispatchers.IO)

}