package uz.promo.selling.domain.useCase.postNewProduct

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import uz.promo.selling.data.remote.models.ModelSuccess
import uz.promo.selling.domain.repository.NetworkRepository
import uz.promo.selling.domain.state.Resource
import java.io.IOException
import javax.inject.Inject

class PostNewProductUseCase @Inject constructor(
    private val repository: NetworkRepository
) {


    operator fun invoke(
        email: String,
        title: String,
        description: String,
        categoryId: Long,
        lat: Double,
        lon: Double,
        addressName: String,
        addressDescription: String,
        userId: Int,
        files: List<MultipartBody.Part>,
        postParams: RequestBody
    ): Flow<Resource<ModelSuccess>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.newProduct(
                        email,
                        title,
                        description,
                        categoryId,
                        lat,
                        lon,
                        addressName,
                        addressDescription,
                        userId,
                        files,
                        postParams
                    ),
                )
            )
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }


}
