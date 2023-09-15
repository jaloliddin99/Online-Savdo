package org.don.onlineTrade.ui.home

import org.don.onlineTrade.data.remote.models.category.CompactedCategoryItem

data class HomeScreenState(
    val isLoading: Boolean = false,
    val registerMain: List<CompactedCategoryItem>?= null,
    val error: String = ""
)
