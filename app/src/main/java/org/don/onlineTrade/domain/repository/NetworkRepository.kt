package org.don.onlineTrade.domain.repository

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.don.onlineTrade.data.remote.models.GenericModel
import org.don.onlineTrade.data.remote.models.LoginBody
import org.don.onlineTrade.data.remote.models.ModelSuccess
import org.don.onlineTrade.data.remote.models.RegistrationBody
import org.don.onlineTrade.data.remote.models.VerificationRes
import org.don.onlineTrade.data.remote.models.category.ParentCategories
import org.don.onlineTrade.data.remote.models.getNotifications.NotificationData
import org.don.onlineTrade.data.remote.models.getProfile.UpdatePasswordModel
import org.don.onlineTrade.data.remote.models.getProfile.UpdateProfileModel
import org.don.onlineTrade.data.remote.models.getProfile.User
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.data.remote.models.leak.ModelLeak
import org.don.onlineTrade.data.remote.models.nearPost.NearPostsData
import org.don.onlineTrade.data.remote.models.post.PostModel
import org.don.onlineTrade.data.remote.models.region.ModelGetRegionAndDistricts
import org.don.onlineTrade.data.remote.models.reverse.ModelAddressReverse
import org.don.onlineTrade.data.remote.models.searchSuggestion.SearchSuggestionData
import org.don.onlineTrade.data.remote.models.showProducts.PostDetailsData

interface NetworkRepository {


    suspend fun register(
        registrationBody: RegistrationBody
    ): ModelSuccess

    suspend fun login(
        loginBody: LoginBody
    ): ModelSuccess

    suspend fun verify(
        code: Int,
        email: String
    ): VerificationRes


    suspend fun forgotPassword(
        email: String
    ): ModelSuccess

    suspend fun resetNewPassword(
        email: String,
        code: Int,
        password: String
    ): ModelSuccess


    suspend fun getAllCategories(
        token: String,
        language: String
    ): org.don.onlineTrade.data.remote.models.category.Category

    suspend fun getAllParentCategories(
        token: String,
        language: String
    ): ParentCategories

    suspend fun getCategoryDetails(
        token: String,
        categoryId: Int
    ): ModelLeak


    suspend fun getAllRegionDistrict(
        token: String,
        language: String
    ): ModelGetRegionAndDistricts

    suspend fun newProduct(
        token: String,
        title: String,
        description: String,
        categoryId: Long,
        regionId: Int,
        districtId: Int,
        lat: Double,
        lon: Double,
        addressName: String,
        addressDescription: String,
        userId: Int,
        files: List<MultipartBody.Part>,
        postParams: RequestBody
    ): ModelSuccess

    suspend fun getNotifications(
        token: String,
        page: Int,
        size: Int,
        lang: String
    ): NotificationData

    suspend fun showProductModel(
        id: Int,
        token: String,
        language: String
    ): GenericModel<PostDetailsData>

    suspend fun deletePost(
        id: Int,
        token: String,
    ): ModelSuccess

    suspend fun likePost(
        id: Int,
        token: String,
        language: String
    ): GenericModel<PostDetailsData>


    suspend fun prioritizePost(
        token: String,
        postId: Long,
        period: Int
    ): ModelSuccess

    suspend fun getLikedProducts(
        token: String,
        page: Int,
        count: Int,
        lang: String
    ): GenericModel<Data>

    suspend fun getProfile(
        token: String
    ): GenericModel<User>

    suspend fun updateProfile(
        token: String,
        body: UpdateProfileModel
    ): ModelSuccess

    suspend fun updateProfileImage(
        token: String,
        body: RequestBody
    ): ModelSuccess

    suspend fun getProductsPager(
        token: String,
        page: Int,
        count: Int,
        lang: String,
        categoryId: Int?,
        query: String?,
        startDate: String?,
        endDate: String?,
        regionId: Int = -1,
        districtId: Int = -1,
        fromPrice: Int? = null,
        toPrice: Int? = null
    ): GenericModel<Data>

    suspend fun getNearPosts(
        token: String,
        lat: Double,
        lon: Double,
        lang: String
    ): GenericModel<List<NearPostsData>>

    suspend fun getMyPostsPager(
        token: String,
        page: Int,
        count: Int,
        lang: String
    ): GenericModel<Data>

    suspend fun updatePassword(token: String, body: UpdatePasswordModel): ModelSuccess

    suspend fun addressReverse(url: String): ModelAddressReverse

    suspend fun getSearchSuggestions(
        query: String,
        lat: Double,
        lon: Double,
        radius: Int,
        lang: String
    ): GenericModel<SearchSuggestionData>

    suspend fun searchPosts(
        lang: String,
        page: Int,
        size: Int,
        query: String,
        lat: Double,
        lon: Double,
        radius: Int,
        categoryId: Long?,
        startDate: String?,
        endDate: String?
    ): GenericModel<Data>

}