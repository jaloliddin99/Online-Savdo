package org.don.onlineTrade.data.repository


import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.don.onlineTrade.data.remote.ApiInterface
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
import org.don.onlineTrade.domain.repository.NetworkRepository
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

    override suspend fun getCategoryDetails(token: String, categoryId: Int): ModelLeak {
        return apiInterface.getCategories(token, categoryId)
    }

    override suspend fun getAllRegionDistrict(
        token: String,
        language: String
    ): ModelGetRegionAndDistricts {
        return apiInterface.getAllRegionDistrict(token, language)
    }


    override suspend fun newProduct(
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
    ): PostModel {
        return apiInterface.newProduct(
            token,
            title,
            description,
            categoryId,
            regionId,
            districtId,
            lat,
            lon,
            addressName,
            addressDescription,
            userId,
            files,
            postParams
        )
    }

    override suspend fun showProductModel(
        id: Int,
        token: String,
        language: String
    ): PostDetailsModel {
        return apiInterface.showProductModel(token, id, language)
    }

    override suspend fun deletePost(id: Int, token: String): ModelSuccess {
        return apiInterface.deletePost(token, id)
    }

    override suspend fun likePost(id: Int, token: String, language: String): PostDetailsModel {
        return apiInterface.likePost(token, id, language)
    }

    override suspend fun getLikedProducts(
        token: String,
        page: Int,
        count: Int,
        lang: String
    ): ModelPosts {
        return apiInterface.getLikedProducts(
            token = token,
            page = page,
            size = count,
            lang = lang
        )
    }

    override suspend fun getProfile(token: String): ModelGetProfile {
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
        query: String?
    ): ModelPosts {
        return apiInterface.getPublicProducts(
            token = token,
            page = page,
            size = count,
            lang = lang,
            categoryId = categoryId,
            query = query
        )
    }

    override suspend fun getNearPosts(
        token: String,
        lat: Double,
        lon: Double,
        lang: String
    ): NeaPostModel {
        return apiInterface.getNearPosts(
            token, lat, lon, lang
        )
    }


    override suspend fun getMyPostsPager(
        token: String,
        page: Int,
        count: Int,
        lang: String
    ): ModelPosts {
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

}