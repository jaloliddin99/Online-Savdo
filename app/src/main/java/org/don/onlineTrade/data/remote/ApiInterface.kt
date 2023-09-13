package org.don.onlineTrade.data.remote

import org.don.onlineTrade.data.remote.models.RegisterMain
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {

    @FormUrlEncoded
    @POST("api/register")
    suspend fun register(
        @Header("Accept") accept: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String,
        @Field("phone_number") phoneNumber: String
    ): RegisterMain


}