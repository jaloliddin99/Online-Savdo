package org.don.onlineTrade.domain.useCase

import org.don.onlineTrade.data.remote.models.getPublicProducts.Content
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.utils.SharedPref
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