package org.don.onlineTrade.data.remote

import androidx.annotation.Keep
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.don.onlineTrade.data.remote.models.GenericModel
import org.don.onlineTrade.data.remote.models.LoginBody
import org.don.onlineTrade.data.remote.models.ModelSuccess
import org.don.onlineTrade.data.remote.models.RegistrationBody
import org.don.onlineTrade.data.remote.models.VerificationRes
import org.don.onlineTrade.data.remote.models.category.Category
import org.don.onlineTrade.data.remote.models.category.ParentCategories
import org.don.onlineTrade.data.remote.models.getNotifications.NotificationData
import org.don.onlineTrade.data.remote.models.getProfile.UpdatePasswordModel
import org.don.onlineTrade.data.remote.models.getProfile.UpdateProfileModel
import org.don.onlineTrade.data.remote.models.getProfile.User
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.data.remote.models.leak.ModelLeak
import org.don.onlineTrade.data.remote.models.nearPost.NearPostsData
import org.don.onlineTrade.data.remote.models.post.PostModel
import org.don.onlineTrade.data.remote.models.reverse.ModelAddressReverse
import org.don.onlineTrade.data.remote.models.searchSuggestion.SearchSuggestionData
import org.don.onlineTrade.data.remote.models.showProducts.PostDetailsData
import org.don.onlineTrade.utils.SharedPref
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

    @POST("auth/refresh-token")
    suspend fun refreshToken(
        @Body body: Map<String, String>
    ): GenericModel<Map<String, String>>

    @GET("post")
    suspend fun getPublicProducts(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("lang") lang: String,
        @Query("category_id") categoryId: Int?,
        @Query("query") query: String?,
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?,
        @Query("fromPrice") fromPrice: Int? = null,
        @Query("toPrice") toPrice: Int? = null,
        @Query("lat") lat: Double? = null,
        @Query("lon") lon: Double? = null,
        @Query("radius") radius: Int? = null
    ): GenericModel<Data>


    @GET("notification/pager")
    suspend fun getAllNotifications(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("lang") lang: String,
    ): NotificationData

    @GET("post/near")
    suspend fun getNearPosts(
        @Header("Authorization") token: String,
        @Query("lat") page: Double,
        @Query("lon") size: Double,
        @Query("lang") lang: String
    ): GenericModel<List<NearPostsData>>

    @GET("post/myPosts")
    suspend fun getMyPosts(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("lang") lang: String
    ): GenericModel<Data>

    @GET("categories")
    suspend fun getAllCategories(
        @Header("Authorization") token: String,
        @Query("lang") language: String,
    ): Category

    @GET("categories/parents")
    suspend fun getAllParentCategories(
        @Header("Authorization") token: String,
        @Query("lang") language: String,
    ): ParentCategories

    @GET("categories/{categoryId}")
    suspend fun getCategories(
        @Header("Authorization") token: String,
        @Path("categoryId") categoryId: Int,
    ): ModelLeak

    @Multipart
    @POST("post")
    suspend fun newProduct(
        @Header("Authorization") token: String,
        @Query("title") title: String,
        @Query("description") description: String,
        @Query("category_id") categoryId: Long,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("addressName") addressName: String,
        @Query("addressDescription") addressDescription: String,
        @Query("userId") userId: Int,
        @Part files: List<MultipartBody.Part>,
        @Part("post_params") postParams: RequestBody
    ): ModelSuccess


    @POST("post/prioritize")
    suspend fun prioritizePost(
        @Header("Authorization") token: String,
        @Query("postId") postId: Long,
        @Query("period") periodInHours: Int
    ): ModelSuccess

    @GET("post/{id}")
    suspend fun showProductModel(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Query("lang") language: String
    ): GenericModel<PostDetailsData>

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
    ): GenericModel<PostDetailsData>

    @GET("post/user/liked-posts")
    suspend fun getLikedProducts(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("lang") lang: String
    ): GenericModel<Data>


    @GET("user")
    suspend fun getProfile(
        @Header("Authorization") token: String,
    ): GenericModel<User>

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

    @GET("post/search/suggest")
    suspend fun getSearchSuggestions(
        @Query("query") query: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("radius") radius: Int,
        @Query("lang") lang: String,
        @Header("Authorization") token: String = SharedPref.deviceToken,
        ): GenericModel<SearchSuggestionData>

    @GET("post/search")
    suspend fun searchPosts(
        @Query("lang") lang: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("query") query: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("radius") radius: Int,
        @Query("category_id") categoryIds: List<Long>?,
        @Query("status") status: Int = 1,
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?,
        @Header("Authorization") token: String = SharedPref.deviceToken,
        ): GenericModel<Data>

    @GET
    suspend fun getLocationList(
        @Url url: String
    ): ModelAddressReverse




}