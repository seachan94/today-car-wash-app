package com.nenne.presentation.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nenne.presentation.data.CarShopType
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserSelectedDataStore(
    val context: Context,
) {

    private val Context.userSelectedDataStore: DataStore<Preferences> by preferencesDataStore("user_selected")

    val mapCarWashShopFilterType = context.userSelectedDataStore.data
        .catch { e->
            if(e is IOException){
                emit(emptyPreferences())
            }else{
                throw e
            }
        }.map {
            when(it[PreferenceKeys.MAP_FILTER_TYPE] ?: 0){
                0 -> CarShopType.ALL
                1 -> CarShopType.AUTO
                else -> CarShopType.SELF
            }
        }


    suspend fun updateMapCarWashShopFilterType(filterType : CarShopType){
        context.userSelectedDataStore.edit {
            val integerType = when(filterType){
                CarShopType.ALL ->0
                CarShopType.AUTO ->1
                else->2
            }
            it[PreferenceKeys.MAP_FILTER_TYPE] = integerType
        }
    }

    private object PreferenceKeys {
        val MAP_FILTER_TYPE = intPreferencesKey("map_filter_type")
    }
}