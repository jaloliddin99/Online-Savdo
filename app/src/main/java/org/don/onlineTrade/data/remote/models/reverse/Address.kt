package org.don.onlineTrade.data.remote.models.reverse


data class Address(
    val Components: List<Component>,
    val country_code: String,
    val formatted: String
)