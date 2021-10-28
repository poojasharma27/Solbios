package com.solbios.ui.fragment.cart.address

import com.solbios.interfaces.ApiServiceIn
import com.solbios.model.addAddress.AddAddressRoot
import com.solbios.model.cart.deleteCartItem.DeleteCartItem
import com.solbios.model.selectAddress.SelectAddressRoot
import com.solbios.model.selectAddress.removeList.DeleteAddressRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SelectAddressRepository @Inject constructor(val apiServiceIn: ApiServiceIn) {

    suspend fun selectAddress(header: String?)= flow<SelectAddressRoot> {
        emit(apiServiceIn.userAddressList(header))

    }.flowOn(Dispatchers.IO)


    suspend fun deleteAddress(header:String?,id:Int?)= flow<DeleteAddressRoot>{
        emit(apiServiceIn.deleteAddress(header,id))

    }.flowOn(Dispatchers.IO)

}