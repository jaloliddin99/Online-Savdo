package uz.promo.selling.domain.useCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.promo.selling.data.remote.models.leak.ModelLeak
import uz.promo.selling.domain.repository.NetworkRepository
import uz.promo.selling.domain.state.Resource
import java.io.IOException
import javax.inject.Inject

class CategoryMainUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    operator fun invoke(
        categoryId: Int
    ): Flow<Resource<ModelLeak>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.getCategoryDetails(
                        categoryId
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}