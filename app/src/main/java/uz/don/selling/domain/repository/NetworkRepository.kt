package uz.don.selling.domain.repository

import okhttp3.MultipartBody
import okhttp3.RequestBody
import uz.don.selling.data.remote.models.GenericModel
import uz.don.selling.data.remote.models.LoginBody
import uz.don.selling.data.remote.models.ModelSuccess
import uz.don.selling.data.remote.models.RegistrationBody
import uz.don.selling.data.remote.models.VerificationRes
import uz.don.selling.data.remote.models.category.ParentCategories
import uz.don.selling.data.remote.models.getNotifications.NotificationData
import uz.don.selling.data.remote.models.getProfile.UpdatePasswordModel
import uz.don.selling.data.remote.models.getProfile.UpdateProfileModel
import uz.don.selling.data.remote.models.getProfile.User
import uz.don.selling.data.remote.models.getPublicProducts.Data
import uz.don.selling.data.remote.models.leak.ModelLeak
import uz.don.selling.data.remote.models.nearPost.NearPostsData
import uz.don.selling.data.remote.models.reverse.ModelAddressReverse
import uz.don.selling.data.remote.models.searchSuggestion.SearchSuggestionData
import uz.don.selling.data.remote.models.showProducts.PostDetailsData

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
        language: String
    ): uz.don.selling.data.remote.models.category.Category

    suspend fun getAllParentCategories(
        language: String
    ): ParentCategories

    suspend fun getCategoryDetails(
        categoryId: Int
    ): ModelLeak


    suspend fun newProduct(
        token: String,
        title: String,
        description: String,
        categoryId: Long,
        lat: Double,
        lon: Double,
        addressName: String,
        addressDescription: String,
        userId: Int,
        files: List<MultipartBody.Part>,
        postParams: RequestBody
    ): ModelSuccess

    suspend fun getNotifications(
        page: Int,
        size: Int,
        lang: String
    ): NotificationData

    suspend fun showProductModel(
        id: Int,
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
        page: Int,
        count: Int,
        lang: String,
        categoryId: Int?,
        query: String?,
        startDate: String?,
        endDate: String?,
        fromPrice: Int? = null,
        toPrice: Int? = null,
        lat: Double? = null,
        lon: Double? = null,
        radius: Int? = null
    ): GenericModel<Data>

    suspend fun getNearPosts(
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


}