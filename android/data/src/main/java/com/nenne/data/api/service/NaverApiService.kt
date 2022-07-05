package com.nenne.data.api.service

import com.nenne.data.model.ReverseGeoCode
import com.nenne.domain.model.state.NetworkResultState
import retrofit2.http.GET
import retrofit2.http.Query

interface NaverApiService {

    @GET("map-reversegeocode/v2/gc")
    suspend fun getLocationReverseGeoCode(
        @Query("coords") coords : String,
        @Query("output") output : String = "json"
    ) : NetworkResultState<ReverseGeoCode>
}