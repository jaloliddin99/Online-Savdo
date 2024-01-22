package org.don.onlineTrade.domain.useCase

import org.don.onlineTrade.data.remote.models.getPublicProducts.Content
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

class GetLikedProductUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    suspend fun getItems(
        token: String = SharedPref.deviceToken,
        page: Int,
        pageSize: Int,
        lang: String = SharedPref.language
    ): Result<List<Content>> {
        return try {
            val networkPager = repository.getLikedProducts(
                token = token,
                page = page,
                count = pageSize,
                lang = lang
            ).data.content
            return Result.success(
                networkPager
            )
        }catch (e: Exception){
            return Result.failure(Throwable("Error occurred!"))
        }
    }


}