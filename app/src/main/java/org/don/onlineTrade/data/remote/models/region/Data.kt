package org.don.onlineTrade.data.remote.models.region

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class ModelGetRegions(
    val `data`: List<Data>,
    val message: String,
    val success: Boolean
)

@Keep
data class ModelGetDistricts(
    val `data`: List<DataDistrict>,
    val message: String,
    val success: Boolean
)
@Keep
data class Data(
    val id: Int,
    val name: String
):Serializable

@Keep
data class DataDistrict(
    val id: Int = -1,
    val name: String = "",
    val regionId: Int = -1
):Serializable

@Keep
data class ModelGetRegionAndDistricts(
    val `data`: List<RegionDistrict>,
    val message: String,
    val success: Boolean
)

@Keep
data class RegionDistrict(
    val id: Int,
    val name: String,
    val regionId: Int?=null
)