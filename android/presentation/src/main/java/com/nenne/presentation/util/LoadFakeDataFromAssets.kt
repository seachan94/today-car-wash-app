package com.nenne.presentation.util

import android.content.Context
import com.google.gson.Gson
import java.io.IOException
import java.lang.reflect.Type

class LoadFakeDataFromAssets(
    private val context : Context
) {
    private val gson = Gson()

     fun <T> getObjectFromJson(fileName : String, type : Type) : T{
        val message = readMockFileToString(fileName)
        return gson.fromJson(message,type)
    }
    private fun readMockFileToString(fileName : String) =
        try{
            val inputStream = context.assets.open(fileName)

            inputStream.bufferedReader()
                .use{
                    it.readText()
                }
        }catch (e : IOException){
            throw(e)
        }
}