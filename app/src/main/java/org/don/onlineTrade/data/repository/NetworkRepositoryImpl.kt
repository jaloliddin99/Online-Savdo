package org.don.onlineTrade.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody
import org.don.onlineTrade.data.remote.ApiInterface
import org.don.onlineTrade.data.remote.models.RegisterMain
import org.don.onlineTrade.data.remote.models.category.CategoryModel
import org.don.onlineTrade.data.remote.models.currencies.ModelCurrencyLists
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.data.remote.models.liked.LikedProductsModel
import org.don.onlineTrade.data.remote.models.post.PostModel
import org.don.onlineTrade.data.remote.models.region.RegionDistrictModel
import org.don.onlineTrade.data.remote.models.showProducts.ShowProductModel
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.utils.DEFAULT_PAGE_SIZE
import org.don.onlineTrade.utils.PublicProductsPagingSource
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NetworkRepositoryImpl @Inject constructor(
    private val apiInterface: ApiInterface
) : NetworkRepository {
    override suspend fun register(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String,
        phoneNumber: String
    ): RegisterMain {
        return apiInterface.register(
            "application/json",
            name,
            email,
            password,
            passwordConfirmation,
            phoneNumber
        )
    }

    override suspend fun login(email: String, password: String): RegisterMain {
        return apiInterface.login(
            "application/json",
            email,
            password
        )
    }

    override fun getPublicProducts(
        token: String,
        query: String?,
        categoryId: Int?,
        language: String,
        minPrice: Int?,
        maxPrice: Int?,
        pagingConfig: PagingConfig
    ): Flow<PagingData<Data>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                PublicProductsPagingSource(
                    token = token,
                    query = query,
                    categoryId = categoryId,
                    language = language,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    doggoApiService = apiInterface
                )
            }
        ).flow
    }

    override suspend fun getAllCategories(token: String, language: String): CategoryModel {
        return apiInterface.getAllCategories(token, language)
    }

    override suspend fun getAllRegions(token: String, language: String): RegionDistrictModel {
        return apiInterface.getRegionDistrict(token, language)
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
    ): ShowProductModel {
        return apiInterface.showProductModel(id, token, language)
    }

    override suspend fun getLikedProducts(token: String, language: String): LikedProductsModel {
        return apiInterface.getLikedProducts(token, language)
    }


}