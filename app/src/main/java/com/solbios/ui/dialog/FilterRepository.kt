package com.solbios.ui.dialog

import com.solbios.interfaces.ApiServiceIn
import com.solbios.model.filter.FilterRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FilterRepository @Inject constructor(val apiServiceIn: ApiServiceIn) {

    suspend fun getFilter()=flow<FilterRoot>{
        emit(apiServiceIn.filterList())
    }.flowOn(Dispatchers.IO)

}