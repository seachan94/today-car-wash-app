package com.nenne.data.mapper

import com.google.common.graph.Network
import com.nenne.domain.model.CarWashData
import com.nenne.domain.model.state.NetworkResultState

object ResponseMapper {

    fun responseToCarWashShopList(response : NetworkResultState<CarWashData>) =
        when(response){
            is NetworkResultState.Success ->NetworkResultState.Success(response.data.items)
            is NetworkResultState.ApiError ->NetworkResultState.ApiError(response.code,response.message)
            is NetworkResultState.NetworkError -> NetworkResultState.NetworkError(response.error)
            is NetworkResultState.Error -> NetworkResultState.Error(response.throwable)
            is NetworkResultState.Loading -> NetworkResultState.Loading
        }
}