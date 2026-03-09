package org.don.onlineTrade.domain.useCase

import org.don.onlineTrade.data.remote.models.getPublicProducts.Content
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

class ProductsPagerUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    suspend fun getItems(
        token: String = SharedPref.deviceToken,
        page: Int,
        pageSize: Int,
        lang: String = SharedPref.language,
        categoryId: Int?,
        query: String?,
        startDate: String?,
        endDate: String?,
        regionId: Int = -1,
        districtId: Int = -1,
        fromPrice: Int? = null,
        toPrice: Int? = null
    ): Result<List<Content>> {
        try {
            val networkPager = repository.getProductsPager(
                token = token,
                page = page,
                count = pageSize,
                lang = lang,
                categoryId = categoryId,
                query = query,
                startDate,
                endDate,
                regionId,
                districtId,
                fromPrice,
                toPrice
            ).data.content
            return Result.success(
                networkPager
            )
        }catch (e: Exception){
            return Result.failure(Throwable("Error occurred!"))
        }

//        return if (startingIndex + pageSize <= networkPager.size) {
//            Result.success(
//                networkPager.slice(startingIndex until startingIndex + pageSize)
//            )
//        } else {
//            Result.success(emptyList())
//        }
    }


}