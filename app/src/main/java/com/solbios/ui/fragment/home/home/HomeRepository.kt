package com.solbios.ui.fragment.home.home

import com.solbios.interfaces.ApiServiceIn
import com.solbios.model.home.PopularCategoriesRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class HomeRepository @Inject constructor( val apiServiceIn: ApiServiceIn) {

    suspend fun homeDetails(header:String?)=flow<PopularCategoriesRoot>{
        emit(apiServiceIn.homeScreen(header))

    }.flowOn(Dispatchers.IO)


}