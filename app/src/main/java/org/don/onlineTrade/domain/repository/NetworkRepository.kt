package org.don.onlineTrade.domain.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.don.onlineTrade.data.remote.models.RegisterMain
import org.don.onlineTrade.data.remote.models.category.CategoryModel
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.utils.DEFAULT_PAGE_SIZE
import retrofit2.http.Query

interface NetworkRepository {


    suspend fun register(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String,
        phoneNumber: String
    ): RegisterMain

    suspend fun login(
        email: String,
        password: String,
    ): RegisterMain

    suspend fun getPublicProducts(
        token: String,
        query: String?,
        categoryId: Int?,
        language: String,
        minPrice: Int?,
        maxPrice: Int?,
        pagingConfig: PagingConfig = getDefaultPageConfig()
    ): LiveData<PagingData<Data>>

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
}