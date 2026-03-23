package uz.don.onlineTrade.utils

//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import uz.don.onlineTrade.data.remote.ApiInterface
//import uz.don.onlineTrade.data.remote.models.getPublicProducts.Content
//import retrofit2.HttpException
//import java.io.IOException
//
//class PublicProductsPagingSource(
//    private val token: String,
//    private val lang: String,
//    private val doggoApiService: ApiInterface
//) :
//    PagingSource<Int, Content>() {
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Content> {
//        val page = params.key ?: DEFAULT_PAGE_INDEX
//        return try {
//            val response =
//                doggoApiService.getPublicProducts(token, page, params.loadSize, lang, categoryId = null, query = null)
//
//            LoadResult.Page(
//                response.data.content,
//                prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1,
//                nextKey = if (response.data.content.isEmpty()) null else page + 1
//            )
//        } catch (exception: IOException) {
//            return LoadResult.Error(exception)
//        } catch (exception: HttpException) {
//            return LoadResult.Error(exception)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, Content>): Int? {
//        return null
//    }
//}
//
//const val DEFAULT_PAGE_INDEX = 1
//const val DEFAULT_PAGE_SIZE = 100