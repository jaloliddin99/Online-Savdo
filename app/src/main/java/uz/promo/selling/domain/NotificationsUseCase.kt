package uz.promo.selling.domain

import uz.promo.selling.data.remote.models.getNotifications.Content
import uz.promo.selling.domain.repository.NetworkRepository
import uz.promo.selling.utils.SharedPref
import javax.inject.Inject

class NotificationsUseCase @Inject constructor(
    private val repository: NetworkRepository
) {

    suspend fun getItems(
        page: Int,
        pageSize: Int,
        lang: String = SharedPref.language,
    ): Result<List<Content>> {
        return try {
            val networkPager = repository.getNotifications(
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
