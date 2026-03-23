package uz.don.onlineTrade.domain.useCase.auth.verify

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.don.onlineTrade.data.remote.models.VerificationRes
import uz.don.onlineTrade.domain.repository.NetworkRepository
import uz.don.onlineTrade.domain.state.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class VerifyUseCase @Inject constructor(
    private val repository: NetworkRepository
) {


    operator fun invoke(
        code: Int,
        email: String
    ): Flow<Resource<VerificationRes>> = flow {
        try {
            emit(Resource.Loading())
            val data = repository.verify(code, email)
            if (data.status){
                emit(Resource.Success(data))
            }else{
                emit(Resource.Error(data.message))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }


}