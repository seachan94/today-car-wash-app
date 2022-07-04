package com.nenne.presentation.util

import com.naver.maps.geometry.LatLng


fun Double.zoomToDistance() =
    when(this){
        in 0.0..10.0 -> 30
        in 10.0..11.0 ->24
        in 11.0..12.0 ->12
        in 12.0..14.0 ->6
        else->4
    }

infix fun Double.getLatLng(longitude : Double) : LatLng =
    LatLng(this,longitude)
