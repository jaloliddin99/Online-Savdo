package uz.promo.selling.domain.useCase.auth.registrationUseCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.promo.selling.data.remote.models.ModelSuccess
import uz.promo.selling.data.remote.models.RegistrationBody
import uz.promo.selling.domain.repository.NetworkRepository
import uz.promo.selling.domain.state.Resource
import java.io.IOException
import javax.inject.Inject

class RegistrationUseCase @Inject constructor(
    private val repository: NetworkRepository
) {


    operator fun invoke(
        registrationBody: RegistrationBody
    ): Flow<Resource<ModelSuccess>> = flow {
        try {
            emit(Resource.Loading())
            val data = repository.register(registrationBody)
            if (data.success){
                emit(Resource.Success(data))
            }else{
                emit(Resource.Error(data.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }


}