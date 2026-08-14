package uz.promo.selling.domain.useCase.presentUseCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.promo.selling.data.remote.models.GenericModel
import uz.promo.selling.data.remote.models.showProducts.PostDetailsData
import uz.promo.selling.domain.repository.NetworkRepository
import uz.promo.selling.domain.state.Resource
import java.io.IOException
import javax.inject.Inject

class PresentProductUseCase @Inject constructor(
    private val repository: NetworkRepository
) {


    operator fun invoke(
        id: Int,
        language: String
    ): Flow<Resource<GenericModel<PostDetailsData>>> = flow {
        try {
            emit(Resource.Loading())
            val data = repository.showProductModel(id, language)
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
