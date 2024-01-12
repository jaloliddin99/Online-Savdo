package org.don.onlineTrade.data.remote.models.leak

data class Parameter(
    val code: String,
    val id: Int,
    val label: String,
    val range: Boolean,
    val type: String,
    val units: List<Unit>,
    val validation: Validation,
    val values: List<Any>
)