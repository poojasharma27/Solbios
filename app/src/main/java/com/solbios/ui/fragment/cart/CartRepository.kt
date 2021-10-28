package com.solbios.ui.fragment.cart

import com.solbios.interfaces.ApiServiceIn
import com.solbios.model.addtocart.AddToCartRoot
import com.solbios.model.cart.CartRoot
import com.solbios.model.cart.deleteCartItem.DeleteCartItem
import com.solbios.model.productList.ProductionList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CartRepository @Inject constructor (val apiServiceIn: ApiServiceIn) {

    suspend fun getCartList(header: String?)= flow<CartRoot>{
        emit(apiServiceIn.cartDetails(header))

    }.flowOn(Dispatchers.IO)

    suspend fun getAddToCart(header:String?,productId:Int?,action:Int?)= flow<AddToCartRoot>{
        emit(apiServiceIn.addToCart(header,productId,action))

    }.flowOn(Dispatchers.IO)

    suspend fun deleteCartItem(header:String?,id:Int?)= flow<DeleteCartItem>{
        emit(apiServiceIn.deleteCartItem(header,id))

    }.flowOn(Dispatchers.IO)


}