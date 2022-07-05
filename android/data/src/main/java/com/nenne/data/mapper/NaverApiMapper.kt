package com.nenne.data.mapper

import com.nenne.data.model.Region

object NaverApiMapper {

    fun getAddressUsingReverseGeoCode(region : Region) =
        "${region.area1.name} ${region.area2.name} ${region.area3.name}"
}