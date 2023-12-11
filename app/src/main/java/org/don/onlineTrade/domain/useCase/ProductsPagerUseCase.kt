package org.don.onlineTrade.domain.useCase

import org.don.onlineTrade.data.remote.models.getPublicProducts.Content
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.ui.home.TOKEN
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

class ProductsPagerUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    suspend fun getItems(
        token: String = SharedPref.deviceToken,
        page: Int,
        pageSize: Int,
        lang: String = SharedPref.language
    ): Result<List<Content>> {
        val startingIndex = page * pageSize
        val networkPager = repository.getProductsPager(
            token = token,
            page = page,
            count = pageSize,
            lang = lang
        ).data.content

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