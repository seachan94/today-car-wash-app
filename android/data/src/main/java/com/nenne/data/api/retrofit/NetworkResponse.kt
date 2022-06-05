package com.nenne.data.api.retrofit

import com.nenne.domain.model.state.NetworkResultState
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class NetworkResponse<T>(
    private val delegate : Call<T>
) :Call<NetworkResultState<T>>{
    override fun enqueue(callback: Callback<NetworkResultState<T>>) {
        return delegate.enqueue( object  : Callback<T>{
            override fun onResponse(call: Call<T>, response: Response<T>) {
                response.body()?.let{
                    when(response.code()){
                        in 200..208 ->{
                            callback.onResponse(
                                this@NetworkResponse,
                                Response.success(NetworkResultState.Success(it))
                            )
                        }
                        in 400..409 ->{
                            callback.onResponse(
                                this@NetworkResponse,
                                Response.success(NetworkResultState.ApiError(response.code(),response.message()))
                            )
                        }
                        in 500..508 ->{
                            callback.onResponse(
                                this@NetworkResponse,
                                Response.success(NetworkResultState.ApiError(response.code(),response.message()))
                            )
                        }
                    }
                }?:callback.onResponse(
                    this@NetworkResponse,
                    Response.success(NetworkResultState.ApiError(response.code(),"BODY EMPTY"))
                )
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(
                    this@NetworkResponse,
                    Response.success(NetworkResultState.NetworkError(t))
                )
                call.cancel()
            }

        })
    }


    override fun clone(): Call<NetworkResultState<T>> =
        NetworkResponse(delegate.clone())

    override fun execute(): Response<NetworkResultState<T>> =
        throw UnsupportedOperationException("ResponseCall does not support execute")

    override fun isExecuted(): Boolean =
        delegate.isExecuted

    override fun cancel() =
        delegate.cancel()
    override fun isCanceled(): Boolean =
        delegate.isCanceled
    override fun request(): Request =
        delegate.request()
}