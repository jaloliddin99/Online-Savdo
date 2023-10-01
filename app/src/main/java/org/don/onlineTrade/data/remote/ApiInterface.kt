package org.don.onlineTrade.data.remote

import androidx.annotation.Keep
import okhttp3.RequestBody
import org.don.onlineTrade.data.remote.models.RegisterMain
import org.don.onlineTrade.data.remote.models.category.CategoryModel
import org.don.onlineTrade.data.remote.models.currencies.ModelCurrencyLists
import org.don.onlineTrade.data.remote.models.getPublicProducts.PublicProductsModel
import org.don.onlineTrade.data.remote.models.liked.LikedProductsModel
import org.don.onlineTrade.data.remote.models.post.PostModel
import org.don.onlineTrade.data.remote.models.region.RegionDistrictModel
import org.don.onlineTrade.data.remote.models.showProducts.ShowProductModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

@Keep
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

    @FormUrlEncoded
    @POST("api/login")
    suspend fun login(
        @Header("Accept") accept: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): RegisterMain


    @GET("api/products")
    suspend fun getPublicProducts(
        @Query("token") token: String,
        @Query("q") query: String?,
        @Query("category_id") category_id: Int?,
        @Query("lang") language: String,
        @Query("page") page: Int,
        @Query("count") count: Int,
        @Query("min_price") minPrice: Int?,
        @Query("max_price") maxPrice: Int?
    ): PublicProductsModel

    @GET("api/categories")
    suspend fun getAllCategories(
        @Query("token") token: String,
        @Query("lang") language: String,
    ): CategoryModel


    @GET("api/regions")
    suspend fun getRegionDistrict(
        @Query("token") token: String,
        @Query("lang") language: String,
    ): RegionDistrictModel


    @GET("api/currencies")
    suspend fun getAllCurrencies(
        @Query("token") token: String,
        @Query("lang") string: String
    ): ModelCurrencyLists


    @Headers("Accept: application/json")
    @POST("api/profile/products")
    suspend fun newProduct(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): PostModel


    @GET("api/products/{id}")
    suspend fun showProductModel(
        @Path("id") id: Int,
        @Query("token") token: String,
        @Query("lang") language: String
    ): ShowProductModel


    @GET("api/profile/products/liked")
    suspend fun getLikedProducts(
        @Header("Authorization") token: String,
        @Query("lang") language: String
    ): LikedProductsModel




}