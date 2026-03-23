package uz.don.onlineTrade.domain

import uz.don.onlineTrade.data.remote.models.getNotifications.Content
import uz.don.onlineTrade.domain.repository.NetworkRepository
import uz.don.onlineTrade.utils.SharedPref
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