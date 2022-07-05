package com.nenne.domain.usecase.carwash

import com.nenne.domain.repository.carwash.CarWashShopLocationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSearchCarWashShopUseCase @Inject constructor(
    private val carWashShopLocationRepository: CarWashShopLocationRepository
)
{
    operator fun invoke(latitude : Double, longitude : Double, distance : Int) =
        carWashShopLocationRepository.getCarWashShopAroundCurrent(latitude,longitude,distance)
}