package com.nenne.domain.usecase.naverapi

import com.nenne.domain.repository.naverapi.NaverMapReverseGeoCodeRepository
import javax.inject.Inject

class GetReverseGeoCodeUseCase @Inject constructor(
    private val naverMapReverseGeoCodeRepository: NaverMapReverseGeoCodeRepository
) {
    operator fun invoke(coords : String) =
        naverMapReverseGeoCodeRepository.getReverseGeoCode(coords)
}