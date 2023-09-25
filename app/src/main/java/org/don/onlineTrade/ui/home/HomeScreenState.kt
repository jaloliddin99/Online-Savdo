package org.don.onlineTrade.ui.home

import org.don.onlineTrade.data.remote.models.category.CompactedCategoryItem
import org.don.onlineTrade.data.remote.models.region.RegionDistrictModelItem

data class HomeScreenState(
    val isLoading: Boolean = false,
    val registerMain: List<CompactedCategoryItem>?= null,
    val error: String = ""
)


data class RegionsScreenState(
    val isLoading: Boolean = false,
    val regions: List<RegionDistrictModelItem>?= null,
    val error: String = ""
)
