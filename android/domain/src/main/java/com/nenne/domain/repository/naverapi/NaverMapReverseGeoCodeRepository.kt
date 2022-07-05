package com.nenne.domain.repository.naverapi

import com.nenne.domain.model.state.NetworkResultState
import kotlinx.coroutines.flow.Flow

interface NaverMapReverseGeoCodeRepository {

    fun getReverseGeoCode( coords : String ) : Flow<NetworkResultState<String>>
}