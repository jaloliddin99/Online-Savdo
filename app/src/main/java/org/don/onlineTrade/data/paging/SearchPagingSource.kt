package org.don.onlineTrade.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.don.onlineTrade.data.remote.ApiInterface
import org.don.onlineTrade.data.remote.models.getPublicProducts.Content
import org.don.onlineTrade.utils.SharedPref

class SearchPagingSource(
    private val apiInterface: ApiInterface,
    private val query: String,
    private val lat: Double,
    private val lon: Double,
    private val radius: Int,
    private val categoryId: Long?,
    private val startDate: String?,
    private val endDate: String?,
) : PagingSource<Int, Content>() {

    override fun getRefreshKey(state: PagingState<Int, Content>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Content> {
        val page = params.key ?: 0
        return try {
            val response = apiInterface.searchPosts(
                lang = SharedPref.language,
                page = page,
                size = params.loadSize,
                query = query,
                lat = lat,
                lon = lon,
                radius = radius,
                categoryId = categoryId,
                startDate = startDate,
                endDate = endDate,
            )
            val data = response.data
            LoadResult.Page(
                data = data.content,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (data.last) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
