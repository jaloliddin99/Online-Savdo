package uz.don.onlineTrade.domain.useCase

import uz.don.onlineTrade.data.remote.models.getPublicProducts.Content
import uz.don.onlineTrade.domain.repository.NetworkRepository
import uz.don.onlineTrade.utils.SharedPref
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
        try {
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