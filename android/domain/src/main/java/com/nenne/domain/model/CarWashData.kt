package com.nenne.domain.model

data class CarWashData(
    val items : List<Item>
)

data class Item(
    val name : String,
    val address : String,
    var type : Int,
    val distance : Double,
    val latitude : Double,
    val longitude : Double,
)

enum class ShopType{
    AUTO, SELF
}
