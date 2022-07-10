package com.nenne.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class CarWashData(
    val items : List<Item>
)
@Serializable
data class Item(
    val name : String,
    val address : String,
    var type : ShopType,
    val distance : Double,
    val latitude : Double,
    val longitude : Double,
){
    fun distanceToString() = "${distance.toInt()} KM"
    fun typeToTextView() = when(type){
        ShopType.AUTO -> "자동 세차"
        else -> "셀프 세차"
    }
}

enum class ShopType{
    AUTO, SELF
}
