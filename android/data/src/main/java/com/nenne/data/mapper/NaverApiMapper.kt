package com.nenne.data.mapper

import android.util.Log
import com.nenne.data.model.Region

object NaverApiMapper {

    fun getAddressUsingReverseGeoCode(region : Region) =
         "${region.area1.alias} ${region.area3.name}"

}