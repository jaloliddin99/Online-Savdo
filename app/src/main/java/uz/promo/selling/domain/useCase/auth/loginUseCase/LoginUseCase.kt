package uz.promo.selling.domain.useCase.auth.loginUseCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.promo.selling.data.remote.models.LoginBody
import uz.promo.selling.data.remote.models.VerificationRes
import uz.promo.selling.domain.repository.NetworkRepository
import uz.promo.selling.domain.state.Resource
import java.io.IOException
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    operator fun invoke(
        loginBody: LoginBody
    ): Flow<Resource<VerificationRes>> = flow {
        try {
            emit(Resource.Loading())
            val data = repository.login(loginBody)
            if (data.status || data.requiresVerification == true){
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