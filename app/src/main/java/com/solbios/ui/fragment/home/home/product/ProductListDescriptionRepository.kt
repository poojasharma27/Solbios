package com.solbios.ui.fragment.home.home.product

import com.solbios.interfaces.ApiServiceIn
import com.solbios.model.addtocart.AddToCartRoot
import com.solbios.model.productDetails.ProductDetailsRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProductListDescriptionRepository  @Inject constructor (val apiServiceIn: ApiServiceIn) {

    suspend fun getDetails(header: String?,id:Int?)= flow<ProductDetailsRoot> {
        emit(apiServiceIn.productDetails(header,id))
    }.flowOn(Dispatchers.IO)

    suspend fun getAddToCart(header:String?,productId:Int?,action:Int?)= flow<AddToCartRoot>{
        emit(apiServiceIn.addToCart(header,productId,action))

    }.flowOn(Dispatchers.IO)


}