package org.don.onlineTrade.domain.useCase.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.don.onlineTrade.data.remote.models.ModelSuccess
import org.don.onlineTrade.data.remote.models.VerificationRes
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.domain.state.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ResetNewPasswordUseCase @Inject constructor(
    private val repository: NetworkRepository
) {

    operator fun invoke(
        email: String,
        code: Int,
        password: String
    ): Flow<Resource<ModelSuccess>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.resetNewPassword(
                        email, code, password
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