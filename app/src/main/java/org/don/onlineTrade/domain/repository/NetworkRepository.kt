package org.don.onlineTrade.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody
import org.don.onlineTrade.data.remote.models.LoginBody
import org.don.onlineTrade.data.remote.models.ModelSuccess
import org.don.onlineTrade.data.remote.models.RegistrationBody
import org.don.onlineTrade.data.remote.models.VerificationRes
import org.don.onlineTrade.data.remote.models.category.CategoryModel
import org.don.onlineTrade.data.remote.models.currencies.ModelCurrencyLists
import org.don.onlineTrade.data.remote.models.getProfile.ModelGetProfile
import org.don.onlineTrade.data.remote.models.getProfile.UpdateProfileModel
import org.don.onlineTrade.data.remote.models.getPublicProducts.Content
import org.don.onlineTrade.data.remote.models.getPublicProducts.ModelPosts
import org.don.onlineTrade.data.remote.models.liked.LikedProductsModel
import org.don.onlineTrade.data.remote.models.post.PostModel
import org.don.onlineTrade.data.remote.models.region.ModelGetDistricts
import org.don.onlineTrade.data.remote.models.region.ModelGetRegions
import org.don.onlineTrade.data.remote.models.showProducts.PostDetailsModel
import org.don.onlineTrade.utils.DEFAULT_PAGE_SIZE

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


    fun getPublicProducts(
        token: String,
        pagingConfig: PagingConfig = getDefaultPageConfig()
    ): Flow<PagingData<Content>>

    /**
     * let's define page size, page size is the only required param, rest is optional
     */
    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = DEFAULT_PAGE_SIZE, enablePlaceholders = true)
    }

    suspend fun getAllCategories(
        token: String,
        language: String
    ): CategoryModel


    suspend fun getAllRegions(
        token: String,
        language: String
    ): ModelGetRegions

    suspend fun getAllDistricts(
        token: String,
        language: String,
        regionId: Int
    ): ModelGetDistricts

    suspend fun getAllCurrencies(
        token: String,
        language: String
    ): ModelCurrencyLists

    suspend fun newProduct(
        token: String,
        requestBody: RequestBody
    ): PostModel

    suspend fun showProductModel(
        id: Int,
        token: String,
        language: String
    ): PostDetailsModel

    suspend fun likePost(
        id: Int,
        token: String,
        language: String
    ): PostDetailsModel


    suspend fun getLikedProducts(
        token: String,
        language: String
    ): LikedProductsModel

    suspend fun getProfile(
        token: String
    ): ModelGetProfile

    suspend fun updateProfile(
        token: String,
        body: UpdateProfileModel
    ): ModelSuccess


    suspend fun getProductsPager(
        token: String,
        page: Int,
        count: Int,
        lang: String
    ): ModelPosts

    suspend fun getMyPostsPager(
        token: String,
        page: Int,
        count: Int,
        lang: String
    ): ModelPosts


}