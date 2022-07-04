package com.nenne.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CarWash(
    val items : List<DataItem>
)

@Serializable
data class DataItem(
    val name : String,
    val address : String,
    var type : Int,
    val distance : Double,
    val latitude : Double,
    val longitude : Double,
)