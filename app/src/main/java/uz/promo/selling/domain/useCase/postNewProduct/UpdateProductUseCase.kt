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

/**
 * Edits an existing post. Mirrors [PostNewProductUseCase]; [files] may be empty,
 * in which case the post keeps the images it already has.
 */
class UpdateProductUseCase @Inject constructor(
    private val repository: NetworkRepository
) {

    operator fun invoke(
        token: String,
        postId: Long,
        title: String,
        description: String,
        categoryId: Long,
        lat: Double,
        lon: Double,
        addressName: String,
        addressDescription: String,
        files: List<MultipartBody.Part>,
        keepImageIds: List<Long>,
        postParams: RequestBody
    ): Flow<Resource<ModelSuccess>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.updateProduct(
                        token,
                        postId,
                        title,
                        description,
                        categoryId,
                        lat,
                        lon,
                        addressName,
                        addressDescription,
                        files,
                        keepImageIds,
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
