package org.don.onlineTrade.domain

import org.don.onlineTrade.data.remote.models.getNotifications.Content
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

class NotificationsUseCase @Inject constructor(
    private val repository: NetworkRepository
) {

    suspend fun getItems(
        token: String = SharedPref.deviceToken,
        page: Int,
        pageSize: Int,
        lang: String = SharedPref.language,
    ): Result<List<Content>> {
        return try {
            val networkPager = repository.getNotifications(
                token = token,
                page = page,
                size = pageSize,
                lang = lang
            ).content
            return Result.success(
                networkPager
            )
        }catch (e: Exception){
            return Result.failure(Throwable("Error occurred!"))
        }
    }
}