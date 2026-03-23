package uz.don.selling.domain.useCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.don.selling.data.remote.models.GenericModel
import uz.don.selling.data.remote.models.nearPost.NearPostsData
import uz.don.selling.domain.repository.NetworkRepository
import uz.don.selling.domain.state.Resource
import java.io.IOException
import javax.inject.Inject

class NearPostsUseCase @Inject constructor(
    private val repository: NetworkRepository
) {


    operator fun invoke(
        token: String,
        lat: Double,
        lon: Double,
        lang: String
    ): Flow<Resource<GenericModel<List<NearPostsData>>>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.getNearPosts(
                        token,
                        lat,
                        lon,
                        lang
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