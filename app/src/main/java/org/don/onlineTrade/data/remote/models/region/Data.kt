package org.don.onlineTrade.data.remote.models.region

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class Data(
    val id: Int,
    val name: String
):Serializable