package com.nenne.data.mapper

import com.nenne.data.mapper.CarWashDataMapper.dataItemToDomainItem
import com.nenne.data.mapper.NaverApiMapper.getAddressUsingReverseGeoCode
import com.nenne.data.model.CarWash
import com.nenne.data.model.ReverseGeoCode
import com.nenne.domain.model.CarWashData
import com.nenne.domain.model.state.NetworkResultState

object ResponseMapper {

    fun responseToCarWashShopList(response : NetworkResultState<CarWash>) =
        when(response){
            is NetworkResultState.Success ->NetworkResultState.Success(dataItemToDomainItem(response.data.items))
            is NetworkResultState.ApiError ->NetworkResultState.ApiError(response.code,response.message)
            is NetworkResultState.NetworkError -> NetworkResultState.NetworkError(response.error)
            is NetworkResultState.Error -> NetworkResultState.Error(response.throwable)
            is NetworkResultState.Loading -> NetworkResultState.Loading
            else -> NetworkResultState.Error()
        }

    fun responseToReverseGeoCode(response : NetworkResultState<ReverseGeoCode>) =
        when(response){
            is NetworkResultState.Success ->NetworkResultState.Success(getAddressUsingReverseGeoCode(response.data.results[0].region))
            is NetworkResultState.ApiError ->NetworkResultState.ApiError(response.code,response.message)
            is NetworkResultState.NetworkError -> NetworkResultState.NetworkError(response.error)
            is NetworkResultState.Error -> NetworkResultState.Error(response.throwable)
            is NetworkResultState.Loading -> NetworkResultState.Loading
            else -> NetworkResultState.Error()
        }
}