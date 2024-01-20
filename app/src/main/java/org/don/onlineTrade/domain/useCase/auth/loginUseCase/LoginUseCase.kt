package org.don.onlineTrade.domain.useCase.auth.loginUseCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.don.onlineTrade.data.remote.models.LoginBody
import org.don.onlineTrade.data.remote.models.ModelSuccess
import org.don.onlineTrade.data.remote.models.RegisterMain
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.domain.state.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: NetworkRepository
) {


    operator fun invoke(
        loginBody: LoginBody
    ): Flow<Resource<ModelSuccess>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.login(
                        loginBody
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