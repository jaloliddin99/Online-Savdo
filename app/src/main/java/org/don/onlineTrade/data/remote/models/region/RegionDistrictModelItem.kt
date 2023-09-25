package org.don.onlineTrade.data.remote.models.region

import org.don.onlineTrade.data.remote.models.region.Children
import java.io.Serializable

data class RegionDistrictModelItem(
    val children: List<Children>,
    val id: Int,
    val title: String
):Serializable