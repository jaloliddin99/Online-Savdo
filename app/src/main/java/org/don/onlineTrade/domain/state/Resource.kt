package org.don.onlineTrade.domain.state

import org.don.onlineTrade.data.remote.models.showProducts.ShowProductModel


sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

sealed class ScreenState<T>(val data: T? = null){
    val isLoading: Boolean = false
    class StateClass<T>(data: T) : ScreenState<T>(data)
    val error: String = ""
}