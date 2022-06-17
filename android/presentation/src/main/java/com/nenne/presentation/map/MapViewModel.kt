package com.nenne.presentation.map

import androidx.lifecycle.ViewModel
import com.nenne.domain.model.CarWashData
import com.nenne.domain.model.Item
import com.nenne.domain.model.state.NetworkResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapViewModel : ViewModel() {

    private var _resultState =
        MutableStateFlow<NetworkResultState<List<Item>>>(NetworkResultState.Success(listOf()))
    val resultState : StateFlow<NetworkResultState<List<Item>>>
        get() = _resultState

    fun mockDataSet(mockData : List<Item>){
        _resultState.value = NetworkResultState.Success(mockData)
    }
}