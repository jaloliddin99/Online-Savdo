package org.don.onlineTrade.domain.useCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.don.onlineTrade.data.remote.models.GenericModel
import org.don.onlineTrade.data.remote.models.nearPost.NearPostsData
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.domain.state.Resource
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