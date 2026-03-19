package org.don.onlineTrade.data.remote

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.don.onlineTrade.utils.AuthEvent
import org.don.onlineTrade.utils.SharedPref
import org.json.JSONObject

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code == 401 && !chain.request().url.encodedPath.contains("/auth/")) {
            val refreshToken = SharedPref.refreshToken
            if (refreshToken.isNotBlank()) {
                response.close()

                val json = JSONObject().put("refreshToken", refreshToken).toString()
                val body = json.toRequestBody("application/json".toMediaType())

                val baseUrl = chain.request().url.scheme + "://" +
                        chain.request().url.host +
                        (if (chain.request().url.port != 80 && chain.request().url.port != 443)
                            ":${chain.request().url.port}" else "")

                val refreshRequest = Request.Builder()
                    .url("$baseUrl/api/v1/auth/refresh-token")
                    .post(body)
                    .build()

                val refreshResponse = chain.proceed(refreshRequest)
                if (refreshResponse.isSuccessful) {
                    val responseBody = refreshResponse.body?.string()
                    refreshResponse.close()
                    val jsonResponse = JSONObject(responseBody ?: "")
                    if (jsonResponse.optBoolean("success")) {
                        val data = jsonResponse.optJSONObject("data")
                        val newToken = data?.optString("token")
                        if (!newToken.isNullOrBlank()) {
                            SharedPref.deviceToken = "Bearer $newToken"
                            SharedPref.loginTime = System.currentTimeMillis()

                            val retryRequest = chain.request().newBuilder()
                                .header("Authorization", "Bearer $newToken")
                                .build()
                            return chain.proceed(retryRequest)
                        }
                    }
                } else {
                    refreshResponse.close()
                }
            }

            AuthEvent.emitUnauthorized()
            return response.newBuilder().code(401).build()
        }
        return response
    }
}
