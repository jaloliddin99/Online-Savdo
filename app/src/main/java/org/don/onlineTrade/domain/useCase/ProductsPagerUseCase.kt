package org.don.onlineTrade.domain.useCase

import android.util.Log
import kotlinx.coroutines.delay
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.ui.home.TOKEN
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

class ProductsPagerUseCase @Inject constructor(
    private val repository: NetworkRepository
) {


    suspend fun getItems(
        token: String = TOKEN,
        query: String?,
        categoryId: Int?,
        language: String = SharedPref.language,
        minPrice: Int?,
        maxPrice: Int?,
        page: Int,
        pageSize: Int
    ): Result<List<Data>> {
        val startingIndex = page * pageSize
        val networkPager = repository.getProductsPager(
            token = token,
            query = query,
            categoryId = categoryId,
            language = language,
            minPrice = minPrice,
            maxPrice = maxPrice,
            page = page,
            count = pageSize
        ).data

//        return if (startingIndex + pageSize <= networkPager.size) {
//            Result.success(
//                networkPager.slice(startingIndex until startingIndex + pageSize)
//            )
//        } else {
//            Result.success(emptyList())
//        }
        return Result.success(
                networkPager
            )
    }


}