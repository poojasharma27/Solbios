package com.solbios.ui.fragment.cart.address

import com.solbios.interfaces.ApiServiceIn
import com.solbios.model.addAddress.AddAddressRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AddAddressRepository @Inject constructor(val apiServiceIn: ApiServiceIn) {

    suspend fun addAddress(header: String?,address:String?,name:String?,state:String?,city:String?,pinCode:String?,addresstype:Int?,contact_number:String?,id:String?)= flow<AddAddressRoot> {
        emit(apiServiceIn.addAddress(header,address,name,state,city,pinCode,addresstype,contact_number,id))
    }.flowOn(Dispatchers.IO)

    suspend fun addAddressWithEdit(header: String?,id: String?)= flow<AddAddressRoot> {
        emit(apiServiceIn.userAddressDetail(header,id))

    }.flowOn(Dispatchers.IO)
}