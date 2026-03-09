package org.don.onlineTrade.domain.useCase.auth.loginUseCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.don.onlineTrade.data.remote.models.LoginBody
import org.don.onlineTrade.data.remote.models.ModelSuccess
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.domain.state.Resource
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
            val data = repository.login(loginBody)
            if (data.success){
                emit(Resource.Success(data))
            }else{
                emit(Resource.Error(data.message))
            }
        } catch(e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }




}