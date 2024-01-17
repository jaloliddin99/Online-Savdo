package org.don.onlineTrade.data.remote.models.reverse

import org.don.onlineTrade.data.remote.models.reverse.Component

data class Address(
    val Components: List<Component>,
    val country_code: String,
    val formatted: String
)