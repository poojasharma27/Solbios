package com.solbios.ui.viewModel.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor( stateHandle: SavedStateHandle) :ViewModel(){

    val type=stateHandle.get<String>("type")

}