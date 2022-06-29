package com.nenne.domain.usecase.carwash

import com.nenne.domain.repository.CarWashShopLocationRepository

class GetSearchCarWashShopUseCase(
    private val carWashShopLocationRepository: CarWashShopLocationRepository
)
{
    operator fun invoke(latitude : Double, longitude : Double, distance : Int) =
        carWashShopLocationRepository.getCarWashShopAroundCurrent(latitude,longitude,distance)
}