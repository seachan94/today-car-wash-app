package com.nenne.data.api.retrofit

import com.nenne.domain.model.state.NetworkResultState
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class NetworkResponseAdapter<T>(
    private val successType : Type
) : CallAdapter<T, Call<NetworkResultState<T>>> {
    override fun responseType(): Type {
        return successType
    }

    override fun adapt(call: Call<T>): Call<NetworkResultState<T>> {
        return NetworkResponse(call)
    }
}