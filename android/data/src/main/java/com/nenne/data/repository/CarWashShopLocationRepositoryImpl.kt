package com.nenne.data.repository

import com.nenne.data.api.service.CarWashService
import com.nenne.data.mapper.ResponseMapper
import com.nenne.domain.model.Item
import com.nenne.domain.model.state.NetworkResultState
import com.nenne.domain.repository.carwash.CarWashShopLocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CarWashShopLocationRepositoryImpl @Inject constructor(
    private val carWashApi: CarWashService,
) : CarWashShopLocationRepository {
    override fun getCarWashShopAroundCurrent(
        latitude: Double,
        longitude: Double,
        distance: Int,
    ): Flow<NetworkResultState<List<Item>>> = flow<NetworkResultState<List<Item>>> {
        emit(NetworkResultState.Loading)
        carWashApi.getCarWashShop(latitude, longitude, distance).run {
            emit(ResponseMapper.responseToCarWashShopList(this))
        }
    }.catch { e->
        emit(NetworkResultState.Error(e))
    }.flowOn(Dispatchers.IO)


}