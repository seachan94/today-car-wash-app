package com.nenne.data.api.service

import com.nenne.data.model.CarWash
import com.nenne.domain.model.CarWashData
import com.nenne.domain.model.state.NetworkResultState
import retrofit2.http.GET
import retrofit2.http.Query

interface CarWashService {
    @GET("/api/car-wash")
    suspend fun getCarWashShop(
        @Query("latitude") latitude : Double,
        @Query("longitude") longitude : Double,
        @Query("distance") distance : Int
    ) : NetworkResultState<CarWash>
}