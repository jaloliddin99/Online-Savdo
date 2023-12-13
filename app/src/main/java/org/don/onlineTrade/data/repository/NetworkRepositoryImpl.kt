package org.don.onlineTrade.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody
import org.don.onlineTrade.data.remote.ApiInterface
import org.don.onlineTrade.data.remote.models.LoginBody
import org.don.onlineTrade.data.remote.models.ModelSuccess
import org.don.onlineTrade.data.remote.models.RegistrationBody
import org.don.onlineTrade.data.remote.models.VerificationRes
import org.don.onlineTrade.data.remote.models.category.CategoryModel
import org.don.onlineTrade.data.remote.models.currencies.ModelCurrencyLists
import org.don.onlineTrade.data.remote.models.getProfile.ModelGetProfile
import org.don.onlineTrade.data.remote.models.getPublicProducts.Content
import org.don.onlineTrade.data.remote.models.getPublicProducts.ModelPosts
import org.don.onlineTrade.data.remote.models.liked.LikedProductsModel
import org.don.onlineTrade.data.remote.models.post.PostModel
import org.don.onlineTrade.data.remote.models.region.ModelGetDistricts
import org.don.onlineTrade.data.remote.models.region.ModelGetRegions
import org.don.onlineTrade.data.remote.models.showProducts.PostDetailsModel
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.utils.PublicProductsPagingSource
import org.don.onlineTrade.utils.SharedPref
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

    override fun getPublicProducts(
        token: String,
        pagingConfig: PagingConfig
    ): Flow<PagingData<Content>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                PublicProductsPagingSource(
                    token = token,
                    doggoApiService = apiInterface,
                    lang = SharedPref.language
                )
            }
        ).flow
    }

    override suspend fun getAllCategories(token: String, language: String): CategoryModel {
        return apiInterface.getAllCategories(token, language)
    }

    override suspend fun getAllRegions(token: String, language: String): ModelGetRegions {
        return apiInterface.getRegionDistrict(token, language)
    }

    override suspend fun getAllDistricts(
        token: String,
        language: String,
        regionId: Int
    ): ModelGetDistricts {
        return apiInterface.getDistricts(token, regionId, language)
    }

    override suspend fun getAllCurrencies(token: String, language: String): ModelCurrencyLists {
        return apiInterface.getAllCurrencies(token, language)
    }

    override suspend fun newProduct(token: String, requestBody: RequestBody): PostModel {
        return apiInterface.newProduct(token, requestBody)
    }

    override suspend fun showProductModel(
        id: Int,
        token: String,
        language: String
    ): PostDetailsModel {
        return apiInterface.showProductModel( token, id, language)
    }

    override suspend fun getLikedProducts(token: String, language: String): LikedProductsModel {
        return apiInterface.getLikedProducts(token, language)
    }

    override suspend fun getProfile(token: String): ModelGetProfile {
        return apiInterface.getProfile(token)
    }

    override suspend fun getProductsPager(
        token: String,
        page: Int,
        count: Int,
        lang: String
    ): ModelPosts {
        return apiInterface.getPublicProducts(
            token = token,
            page = page,
            size = count,
            lang = lang
        )
    }


}