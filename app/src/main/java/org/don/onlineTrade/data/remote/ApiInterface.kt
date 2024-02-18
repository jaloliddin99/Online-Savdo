package org.don.onlineTrade.data.remote

import androidx.annotation.Keep
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.don.onlineTrade.data.remote.models.LoginBody
import org.don.onlineTrade.data.remote.models.ModelSuccess
import org.don.onlineTrade.data.remote.models.RegistrationBody
import org.don.onlineTrade.data.remote.models.VerificationRes
import org.don.onlineTrade.data.remote.models.category.Category
import org.don.onlineTrade.data.remote.models.getProfile.ModelGetProfile
import org.don.onlineTrade.data.remote.models.getProfile.UpdatePasswordModel
import org.don.onlineTrade.data.remote.models.getProfile.UpdateProfileModel
import org.don.onlineTrade.data.remote.models.getPublicProducts.ModelPosts
import org.don.onlineTrade.data.remote.models.leak.ModelLeak
import org.don.onlineTrade.data.remote.models.nearPost.NeaPostModel
import org.don.onlineTrade.data.remote.models.post.PostModel
import org.don.onlineTrade.data.remote.models.region.ModelGetRegionAndDistricts
import org.don.onlineTrade.data.remote.models.reverse.ModelAddressReverse
import org.don.onlineTrade.data.remote.models.showProducts.PostDetailsModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

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

    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Query("email") email: String
    ): ModelSuccess

    @POST("auth/reset-new-password")
    suspend fun resetNewPassword(
        @Query("email") email: String,
        @Query("code") code: Int,
        @Query("password") password: String
    ): ModelSuccess

    @GET("post")
    suspend fun getPublicProducts(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("lang") lang: String,
        @Query("category_id") categoryId: Int?,
        @Query("query") query: String?
    ): ModelPosts

    @GET("post/near")
    suspend fun getNearPosts(
        @Header("Authorization") token: String,
        @Query("lat") page: Double,
        @Query("lon") size: Double,
        @Query("lang") lang: String
    ): NeaPostModel

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
    ): Category


    @GET("categories/{categoryId}")
    suspend fun getCategories(
        @Header("Authorization") token: String,
        @Path("categoryId") categoryId: Int,
    ): ModelLeak

    @GET("getRegions/all")
    suspend fun getAllRegionDistrict(
        @Header("Authorization") token: String,
        @Query("lang") language: String,
    ): ModelGetRegionAndDistricts



    @Multipart
    @POST("post")
    suspend fun newProduct(
        @Header("Authorization") token: String,
        @Query("title") title: String,
        @Query("description") description: String,
        @Query("category_id") categoryId: Long,
        @Query("region_id") regionId: Int,
        @Query("district_id") districtId: Int,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("addressName") addressName: String,
        @Query("addressDescription") addressDescription: String,
        @Query("userId") userId: Int,
        @Part files: List<MultipartBody.Part>,
        @Part("post_params") postParams: RequestBody
    ): PostModel


    @GET("post/{id}")
    suspend fun showProductModel(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Query("lang") language: String
    ): PostDetailsModel

    @DELETE("post/{id}")
    suspend fun deletePost(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): ModelSuccess

    @POST("post/{id}/like")
    suspend fun likePost(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Query("lang") language: String
    ): PostDetailsModel

    @GET("post/user/liked-posts")
    suspend fun getLikedProducts(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("lang") lang: String
    ): ModelPosts


    @GET("user")
    suspend fun getProfile(
        @Header("Authorization") token: String,
    ): ModelGetProfile

    @POST("user/update-profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body body: UpdateProfileModel
    ): ModelSuccess

    @POST("user/update-profile-image")
    suspend fun updateProfileImage(
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody
    ): ModelSuccess

    @POST("user/update-password")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body body: UpdatePasswordModel
    ): ModelSuccess

    @GET
    suspend fun getLocationList(
        @Url url: String
    ): ModelAddressReverse




}