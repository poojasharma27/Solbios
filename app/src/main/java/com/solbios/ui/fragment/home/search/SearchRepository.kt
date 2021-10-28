package com.solbios.ui.fragment.home.search

import com.solbios.interfaces.ApiServiceIn
import com.solbios.model.search.SearchRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SearchRepository @Inject constructor( val apiServiceIn: ApiServiceIn){

    suspend fun getSearch(header:String?,keyword:String?)= flow<SearchRoot> {
        emit(apiServiceIn.search(header,keyword))

    }.flowOn(Dispatchers.IO)

}