package uz.don.selling.domain.useCase

import uz.don.selling.data.remote.models.getPublicProducts.Content
import uz.don.selling.domain.repository.NetworkRepository
import uz.don.selling.utils.SharedPref
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