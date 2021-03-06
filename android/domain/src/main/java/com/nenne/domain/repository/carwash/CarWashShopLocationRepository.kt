package com.nenne.domain.repository.carwash


import com.nenne.domain.model.Item
import com.nenne.domain.model.state.NetworkResultState
import kotlinx.coroutines.flow.Flow

interface CarWashShopLocationRepository {
    fun getCarWashShopAroundCurrent(
        latitude : Double, longitude : Double, distance : Int) : Flow<NetworkResultState<List<Item>>>
}