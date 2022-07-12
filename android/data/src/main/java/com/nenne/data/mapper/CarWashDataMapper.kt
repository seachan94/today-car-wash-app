package com.nenne.data.mapper

import com.nenne.domain.model.Item
import com.nenne.domain.model.ShopType
import com.nenne.data.model.DataItem

object CarWashDataMapper {
    fun dataItemToDomainItem(data: List<DataItem>) =
        data.sortedBy { it.distance }
            .map {
                when (it.type) {
                    1 -> Item(it.name, it.address, ShopType.SELF, it.distance, it.latitude, it.longitude)
                    else -> Item(it.name, it.address, ShopType.AUTO, it.distance, it.latitude, it.longitude)
                }
            }
}