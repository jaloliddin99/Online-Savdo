package uz.promo.selling.domain.useCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.promo.selling.data.remote.models.GenericModel
import uz.promo.selling.data.remote.models.nearPost.NearPostsData
import uz.promo.selling.domain.repository.NetworkRepository
import uz.promo.selling.domain.state.Resource
import java.io.IOException
import javax.inject.Inject

class NearPostsUseCase @Inject constructor(
    private val repository: NetworkRepository
) {


    operator fun invoke(
        lat: Double,
        lon: Double,
        lang: String,
        radius: Int? = null
    ): Flow<Resource<GenericModel<List<NearPostsData>>>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.getNearPosts(
                        lat,
                        lon,
                        lang,
                        radius
                    )
                )
            )
        } catch(e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }




}
