package uz.promo.selling.domain.useCase

import uz.promo.selling.data.remote.models.getPublicProducts.Content
import uz.promo.selling.domain.repository.NetworkRepository
import uz.promo.selling.utils.SharedPref
import javax.inject.Inject

class MyPostsUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    suspend fun getItems(
        token: String = SharedPref.deviceToken,
        page: Int,
        pageSize: Int,
        lang: String = SharedPref.language
    ): Result<List<Content>> {
        val networkPager = repository.getMyPostsPager(
            token = token,
            page = page,
            count = pageSize,
            lang = lang
        ).data.content
        return Result.success(
            networkPager
        )
    }


}