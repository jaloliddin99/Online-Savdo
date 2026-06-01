package uz.promo.selling.domain.useCase

import uz.promo.selling.data.remote.models.getPublicProducts.Content
import uz.promo.selling.domain.repository.NetworkRepository
import uz.promo.selling.utils.SharedPref
import javax.inject.Inject

class ProductsPagerUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    suspend fun getItems(
        page: Int,
        pageSize: Int,
        lang: String = SharedPref.language,
        categoryId: Int?,
        query: String?,
        startDate: String?,
        endDate: String?,
        fromPrice: Int? = null,
        toPrice: Int? = null,
        lat: Double? = null,
        lon: Double? = null,
        radius: Int? = null
    ): Result<List<Content>> {
        try {
            val networkPager = repository.getProductsPager(
                page = page,
                count = pageSize,
                lang = lang,
                categoryId = categoryId,
                query = query,
                startDate,
                endDate,
                fromPrice,
                toPrice,
                lat,
                lon,
                radius
            ).data.content
            return Result.success(
                networkPager
            )
        }catch (e: Exception){
            return Result.failure(Throwable("Error occurred!"))
        }
    }


}
