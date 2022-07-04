package com.nenne.domain.model.state

sealed class NetworkResultState<out T> {
    object Loading : NetworkResultState<Nothing>()
    object InitState : NetworkResultState<Nothing>()
    data class Success<T>(val data : T): NetworkResultState<T>()
    data class Error<T>(val throwable: Throwable? = null) : NetworkResultState<T>()
    data class ApiError<T>(val code : Int,val message : String) : NetworkResultState<T>()
    data class NetworkError<T>(val error : Throwable) : NetworkResultState<T>()

}
