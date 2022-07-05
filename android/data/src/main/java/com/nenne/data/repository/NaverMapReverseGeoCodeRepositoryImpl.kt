package com.nenne.data.repository

import android.util.Log
import com.nenne.data.api.service.NaverApiService
import com.nenne.data.mapper.ResponseMapper
import com.nenne.domain.model.state.NetworkResultState
import com.nenne.domain.repository.naverapi.NaverMapReverseGeoCodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class NaverMapReverseGeoCodeRepositoryImpl @Inject constructor(
    private val naverApiService: NaverApiService
) : NaverMapReverseGeoCodeRepository{

    override fun getReverseGeoCode(coords: String): Flow<NetworkResultState<String>> = flow {
        emit(NetworkResultState.Loading)
        naverApiService.getLocationReverseGeoCode(coords).run {
            emit(ResponseMapper.responseToReverseGeoCode(this))
        }
    }.catch { e->
        emit(NetworkResultState.Error(e))
    }.flowOn(Dispatchers.IO)
}