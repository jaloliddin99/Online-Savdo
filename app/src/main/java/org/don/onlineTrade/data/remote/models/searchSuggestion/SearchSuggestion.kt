package org.don.onlineTrade.data.remote.models.searchSuggestion

data class SearchSuggestionData(
    val queryText: String,
    val categories: List<CategorySuggestion>
)

data class CategorySuggestion(
    val categoryId: Long,
    val name: String,
    val count: Long
)
