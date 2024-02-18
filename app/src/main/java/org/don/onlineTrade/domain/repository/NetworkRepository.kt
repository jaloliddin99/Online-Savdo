package org.don.onlineTrade.domain.repository

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.don.onlineTrade.data.remote.models.LoginBody
import org.don.onlineTrade.data.remote.models.ModelSuccess
import org.don.onlineTrade.data.remote.models.RegistrationBody
import org.don.onlineTrade.data.remote.models.VerificationRes
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
import retrofit2.http.Query

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
    ): PostModel

    suspend fun showProductModel(
        id: Int,
        token: String,
        language: String
    ): PostDetailsModel

    suspend fun deletePost(
        id: Int,
        token: String,
    ): ModelSuccess

    suspend fun likePost(
        id: Int,
        token: String,
        language: String
    ): PostDetailsModel


    suspend fun getLikedProducts(
        token: String,
        page: Int,
        count: Int,
        lang: String
    ): ModelPosts

    suspend fun getProfile(
        token: String
    ): ModelGetProfile

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
        districtId: Int = -1
    ): ModelPosts

    suspend fun getNearPosts(
        token: String,
        lat: Double,
        lon: Double,
        lang: String
    ): NeaPostModel

    suspend fun getMyPostsPager(
        token: String,
        page: Int,
        count: Int,
        lang: String
    ): ModelPosts

    suspend fun updatePassword(token: String, body: UpdatePasswordModel): ModelSuccess

    suspend fun addressReverse(url: String): ModelAddressReverse

}