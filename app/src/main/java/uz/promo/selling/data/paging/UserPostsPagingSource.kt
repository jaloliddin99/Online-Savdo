package uz.promo.selling.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.data.remote.models.getPublicProducts.Content
import uz.promo.selling.utils.SharedPref

/** Live posts of one seller for the public seller profile screen. */
class UserPostsPagingSource(
    private val apiInterface: ApiInterface,
    private val userId: Int,
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
            val response = apiInterface.getUserPosts(
                userId = userId,
                page = page,
                size = params.loadSize,
                lang = SharedPref.language
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
