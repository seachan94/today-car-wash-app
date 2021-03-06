package com.nenne.presentation.model

import com.nenne.domain.model.ShopType
import ted.gun0912.clustering.clustering.TedClusterItem
import ted.gun0912.clustering.geometry.TedLatLng

data class ClusteredItem(
    val name : String,
    val address : String,
    var type : ShopType,
    val distance : Double,
    val latitude : Double,
    val longitude : Double,
):TedClusterItem,java.io.Serializable {
    override fun getTedLatLng(): TedLatLng {
        return TedLatLng(latitude,longitude)
    }
    fun distanceToString() = "${distance.toInt()} KM"

    fun typeToTextView() = when(type){
        ShopType.AUTO -> "자동 세차"
        else -> "셀프 세차"
    }
}
