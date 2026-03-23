package uz.don.selling.data.repository


import okhttp3.MultipartBody
import okhttp3.RequestBody
import uz.don.selling.data.remote.ApiInterface
import uz.don.selling.data.remote.models.GenericModel
import uz.don.selling.data.remote.models.LoginBody
import uz.don.selling.data.remote.models.ModelSuccess
import uz.don.selling.data.remote.models.RegistrationBody
import uz.don.selling.data.remote.models.VerificationRes
import uz.don.selling.data.remote.models.category.Category
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
import uz.don.selling.domain.repository.NetworkRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NetworkRepositoryImpl @Inject constructor(
    private val apiInterface: ApiInterface
) : NetworkRepository {
    override suspend fun register(
        registrationBody: RegistrationBody
    ): ModelSuccess {
        return apiInterface.register(
            registrationBody
        )
    }

    override suspend fun login(loginBody: LoginBody): ModelSuccess {
        return apiInterface.login(
            loginBody
        )
    }

    override suspend fun verify(code: Int, email: String): VerificationRes {
        return apiInterface.verify(
            code, email
        )
    }

    override suspend fun forgotPassword(email: String): ModelSuccess {
        return apiInterface.forgotPassword(email)
    }

    override suspend fun resetNewPassword(
        email: String,
        code: Int,
        password: String
    ): ModelSuccess {
        return apiInterface.resetNewPassword(email, code, password)
    }

//    override fun getPublicProducts(
//        token: String,
//        pagingConfig: PagingConfig
//    ): Flow<PagingData<Content>> {
//        return Pager(
//            config = pagingConfig,
//            pagingSourceFactory = {
//                PublicProductsPagingSource(
//                    token = token,
//                    doggoApiService = apiInterface,
//                    lang = SharedPref.language
//                )
//            }
//        ).flow
//    }

    override suspend fun getAllCategories(token: String, language: String): Category {
        return apiInterface.getAllCategories(token, language)
    }

    override suspend fun getAllParentCategories(token: String, language: String): ParentCategories {
        return apiInterface.getAllParentCategories(token, language)
    }

    override suspend fun getCategoryDetails(token: String, categoryId: Int): ModelLeak {
        return apiInterface.getCategories(token, categoryId)
    }

    override suspend fun newProduct(
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
    ): ModelSuccess {
        return apiInterface.newProduct(
            token,
            title,
            description,
            categoryId,
            lat,
            lon,
            addressName,
            addressDescription,
            userId,
            files,
            postParams
        )
    }

    override suspend fun getNotifications(
        token: String,
        page: Int,
        size: Int,
        lang: String
    ): NotificationData {
        return apiInterface.getAllNotifications(token, page, size, lang)
    }

    override suspend fun showProductModel(
        id: Int,
        token: String,
        language: String
    ): GenericModel<PostDetailsData> {
        return apiInterface.showProductModel(token, id, language)
    }

    override suspend fun deletePost(id: Int, token: String): ModelSuccess {
        return apiInterface.deletePost(token, id)
    }

    override suspend fun likePost(id: Int, token: String, language: String): GenericModel<PostDetailsData> {
        return apiInterface.likePost(token, id, language)
    }

    override suspend fun prioritizePost(token: String, postId: Long, period: Int): ModelSuccess {
        return apiInterface.prioritizePost(token, postId, period)
    }

    override suspend fun getLikedProducts(
        token: String,
        page: Int,
        count: Int,
        lang: String
    ): GenericModel<Data> {
        return apiInterface.getLikedProducts(
            token = token,
            page = page,
            size = count,
            lang = lang
        )
    }

    override suspend fun getProfile(token: String): GenericModel<User> {
        return apiInterface.getProfile(token)
    }

    override suspend fun updateProfile(token: String, body: UpdateProfileModel): ModelSuccess {
        return apiInterface.updateProfile(token, body)
    }

    override suspend fun updateProfileImage(token: String, body: RequestBody): ModelSuccess {
        return apiInterface.updateProfileImage(token, body)
    }

    override suspend fun getProductsPager(
        token: String,
        page: Int,
        count: Int,
        lang: String,
        categoryId: Int?,
        query: String?,
        startDate: String?,
        endDate: String?,
        fromPrice: Int?,
        toPrice: Int?,
        lat: Double?,
        lon: Double?,
        radius: Int?
    ): GenericModel<Data> {
        return apiInterface.getPublicProducts(
            token = token,
            page = page,
            size = count,
            lang = lang,
            categoryId = categoryId,
            query = query,
            startDate,
            endDate,
            fromPrice,
            toPrice,
            lat,
            lon,
            radius
        )
    }

    override suspend fun getNearPosts(
        token: String,
        lat: Double,
        lon: Double,
        lang: String
    ): GenericModel<List<NearPostsData>> {
        return apiInterface.getNearPosts(
            token, lat, lon, lang
        )
    }


    override suspend fun getMyPostsPager(
        token: String,
        page: Int,
        count: Int,
        lang: String
    ): GenericModel<Data> {
        return apiInterface.getMyPosts(
            token = token,
            page = page,
            size = count,
            lang = lang
        )
    }

    override suspend fun updatePassword(token: String, body: UpdatePasswordModel): ModelSuccess {
        return apiInterface.updatePassword(token, body)
    }

    override suspend fun addressReverse(url: String): ModelAddressReverse {
        return apiInterface.getLocationList(url)
    }

    override suspend fun getSearchSuggestions(
        query: String,
        lat: Double,
        lon: Double,
        radius: Int,
        lang: String
    ): GenericModel<SearchSuggestionData> {
        return apiInterface.getSearchSuggestions(query, lat, lon, radius, lang)
    }



}