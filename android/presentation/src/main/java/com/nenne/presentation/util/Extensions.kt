package com.nenne.presentation.util

import com.naver.maps.geometry.LatLng
import com.nenne.domain.model.Item
import com.nenne.presentation.model.ClusteredItem


fun Double.zoomToDistance() =
    when(this){
        in 0.0..10.0 -> 30
        in 10.0..11.0 ->24
        in 11.0..12.0 ->12
        in 12.0..14.0 ->6
        else->4
    }

fun Double.getRadiusForRange() =
    this.zoomToDistance()*1000.0

fun Item.getClusteredItem() =
    ClusteredItem(this.name,this.address,this.type, this.distance,this.latitude,this.longitude)


infix fun Double.getLatLng(longitude : Double) : LatLng =
    LatLng(this,longitude)




