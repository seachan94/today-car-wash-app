package com.nenne.domain.model

data class CarWashData(
    val item : List<Item>
)

data class Item(
    val name : String,
    val address : String,
    var type : ShopType,
    val distance : Double,
    val latitude : Double,
    val longtitude : Double,
    var num : Int
)

enum class ShopType{
    AUTO, SELF
}
