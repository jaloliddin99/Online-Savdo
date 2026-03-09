package org.don.onlineTrade.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import org.don.onlineTrade.utils.AuthEvent

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == 401) {
            AuthEvent.emitUnauthorized()
        }
        return response
    }
}
