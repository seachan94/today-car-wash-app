package com.nenne.presentation.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.nenne.domain.model.CarWashData
import com.nenne.domain.model.Item
import com.nenne.domain.model.state.NetworkResultState
import com.nenne.domain.usecase.carwash.GetSearchCarWashShopUseCase
import com.nenne.domain.usecase.naverapi.GetReverseGeoCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getSearchCarWashShopUseCase: GetSearchCarWashShopUseCase,
    private val getReverseGeoCodeUseCase: GetReverseGeoCodeUseCase,
) : ViewModel() {

    val TAG = "sechan"

    private var _resultState =
        MutableStateFlow<NetworkResultState<List<Item>>>(NetworkResultState.InitState)
    val resultState: StateFlow<NetworkResultState<List<Item>>>
        get() = _resultState

    var isFirstBooting = false
    var resultReverseGeoCode = MutableStateFlow( "위치 찾는 중")
    lateinit var currentLocation: LatLng
    lateinit var detailData: Item

    fun getCarWashShopAroundHere(latitude: Double, longitude: Double, distance: Int = 4) =
        viewModelScope.launch {
            getSearchCarWashShopUseCase(latitude, longitude, distance).collect {
                _resultState.value = it
            }
        }

    fun getReverseGeoCode() {
        viewModelScope.launch {
            getReverseGeoCodeUseCase("${currentLocation.longitude},${currentLocation.latitude}").collect {
                Log.d(TAG, "getReverseGeoCode: $it")
                when (it) {
                    is NetworkResultState.Success -> {
                        resultReverseGeoCode.value = it.data
                    }
                    is NetworkResultState.Loading -> {
                        resultReverseGeoCode.value = "내 위치 찾는 중"
                    }
                    else -> {
                        resultReverseGeoCode.value = "오류가 있습니다. 잠시 후 시도해주세요"
                    }
                }
            }
        }
    }

    fun sortByDistance(data: List<Item>) =
        data.sortedBy { it.distance }


}