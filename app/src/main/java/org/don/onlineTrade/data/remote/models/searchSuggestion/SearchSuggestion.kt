package org.don.onlineTrade.data.remote.models.searchSuggestion

data class SearchSuggestionResponse(
    val success: Boolean,
    val message: String,
    val data: SearchSuggestionData
)

data class SearchSuggestionData(
    val queryText: String,
    val categories: List<CategorySuggestion>
)

data class CategorySuggestion(
    val categoryId: Long,
    val name: String,
    val count: Long
)
