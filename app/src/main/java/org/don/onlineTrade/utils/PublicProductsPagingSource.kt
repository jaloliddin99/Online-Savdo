package org.don.onlineTrade.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.don.onlineTrade.data.remote.ApiInterface
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import retrofit2.HttpException
import java.io.IOException

class PublicProductsPagingSource (private val token: String,
                                  private val query:String?,
                                  private val categoryId: Int?,
                                  private val language: String,
                                  private val minPrice: Int?,
                                  private val maxPrice: Int?,
                                  private val doggoApiService: ApiInterface
                                  ) :
    PagingSource<Int, Data>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Data> {
        //for first case it will be null, then we can pass some default value, in our case it's 1
        val page = params.key ?: DEFAULT_PAGE_INDEX
        return try {
            val response =
                doggoApiService.getPublicProducts(token,query, categoryId, language, page, params.loadSize, minPrice, maxPrice)

            LoadResult.Page(
                response.data,
                prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1,
                nextKey = if (response.data.isEmpty()) null else page + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Data>): Int? {
        return null
    }
}

const val DEFAULT_PAGE_INDEX = 1
const val DEFAULT_PAGE_SIZE = 100