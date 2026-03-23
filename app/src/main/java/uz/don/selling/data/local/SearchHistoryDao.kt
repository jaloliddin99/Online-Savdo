package uz.don.selling.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 5")
    fun getRecentHistory(): Flow<List<SearchHistoryEntity>>

    @Insert
    suspend fun insert(entity: SearchHistoryEntity)

    @Query("""
        DELETE FROM search_history
        WHERE id NOT IN (SELECT id FROM search_history ORDER BY timestamp DESC LIMIT 5)
    """)
    suspend fun trimToLatest()

    @Query("""
        UPDATE search_history SET timestamp = :timestamp
        WHERE query = :query AND COALESCE(categoryId, -1) = COALESCE(:categoryId, -1)
    """)
    suspend fun updateTimestamp(query: String, categoryId: Long?, timestamp: Long = System.currentTimeMillis()): Int

    @Query("DELETE FROM search_history")
    suspend fun clearAll()

    suspend fun upsert(entity: SearchHistoryEntity) {
        val updated = updateTimestamp(entity.query, entity.categoryId)
        if (updated == 0) {
            insert(entity)
            trimToLatest()
        }
    }
}
