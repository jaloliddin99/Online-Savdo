package org.don.onlineTrade.data.remote

import androidx.annotation.Keep
import okhttp3.RequestBody
import org.don.onlineTrade.data.remote.models.LoginBody
import org.don.onlineTrade.data.remote.models.ModelSuccess
import org.don.onlineTrade.data.remote.models.RegistrationBody
import org.don.onlineTrade.data.remote.models.VerificationRes
import org.don.onlineTrade.data.remote.models.category.CategoryModel
import org.don.onlineTrade.data.remote.models.currencies.ModelCurrencyLists
import org.don.onlineTrade.data.remote.models.getProfile.ModelGetProfile
import org.don.onlineTrade.data.remote.models.getPublicProducts.ModelPosts
import org.don.onlineTrade.data.remote.models.liked.LikedProductsModel
import org.don.onlineTrade.data.remote.models.post.PostModel
import org.don.onlineTrade.data.remote.models.region.ModelGetDistricts
import org.don.onlineTrade.data.remote.models.region.ModelGetRegions
import org.don.onlineTrade.data.remote.models.showProducts.PostDetailsModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

@Keep
interface ApiInterface {

    @POST("auth/register")
    suspend fun register(
        @Body registrationBody: RegistrationBody
    ): ModelSuccess

    @POST("auth/authenticate")
    suspend fun login(
        @Body loginBody: LoginBody
    ): ModelSuccess

    @GET("auth/verify")
    suspend fun verify(
        @Query("code") code: Int,
        @Query("email") email: String
    ): VerificationRes


    @GET("post")
    suspend fun getPublicProducts(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("lang") lang: String
    ): ModelPosts

    @GET("post/myPosts")
    suspend fun getMyPosts(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("lang") lang: String
    ): ModelPosts

    @GET("categories")
    suspend fun getAllCategories(
        @Header("Authorization") token: String,
        @Query("lang") language: String,
    ): CategoryModel


    @GET("getRegions")
    suspend fun getRegionDistrict(
        @Header("Authorization") token: String,
        @Query("lang") language: String,
    ): ModelGetRegions

    @GET("getRegions/{regionId}")
    suspend fun getDistricts(
        @Header("Authorization") token: String,
        @Path("regionId") regionId: Int,
        @Query("lang") language: String,
    ): ModelGetDistricts


    @GET("api/currencies")
    suspend fun getAllCurrencies(
        @Query("token") token: String,
        @Query("lang") string: String
    ): ModelCurrencyLists


    @POST("post")
    suspend fun newProduct(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): PostModel


    @GET("post/{id}")
    suspend fun showProductModel(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Query("lang") language: String
    ): PostDetailsModel




    @GET("api/profile/products/liked")
    suspend fun getLikedProducts(
        @Header("Authorization") token: String,
        @Query("lang") language: String
    ): LikedProductsModel


    @GET("user")
    suspend fun getProfile(
        @Header("Authorization") token: String,
    ): ModelGetProfile



}