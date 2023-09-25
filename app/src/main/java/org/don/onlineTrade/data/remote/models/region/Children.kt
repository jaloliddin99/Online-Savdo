package org.don.onlineTrade.data.remote.models.region

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Children(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String
):Serializable