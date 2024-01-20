package org.don.onlineTrade.domain.useCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.don.onlineTrade.data.remote.models.ModelSuccess
import org.don.onlineTrade.data.remote.models.getProfile.ModelGetProfile
import org.don.onlineTrade.data.remote.models.getProfile.UpdatePasswordModel
import org.don.onlineTrade.data.remote.models.getProfile.UpdateProfileModel
import org.don.onlineTrade.data.remote.models.reverse.FeatureMember
import org.don.onlineTrade.data.remote.models.reverse.ModelAddressReverse
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.domain.state.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class LocationReverseUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    operator fun invoke(
        url: String,
    ): Flow<Resource<List<FeatureMember>>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.addressReverse(
                        url
                    ).response.GeoObjectCollection.featureMember
                )
            )
        } catch(e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }


}