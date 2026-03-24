package uz.don.selling.domain.useCase.allCategoriesUseCase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.don.selling.data.remote.models.category.Category
import uz.don.selling.data.remote.models.category.ParentCategories
import uz.don.selling.domain.repository.NetworkRepository
import uz.don.selling.domain.state.Resource
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class AllCategoriesUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    operator fun invoke(
        language: String
    ): Flow<Resource<Category>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.getAllCategories(
                        language,
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    fun parentCategories(
        language: String
    ): Flow<Resource<ParentCategories>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    repository.getAllParentCategories(
                        language,
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
