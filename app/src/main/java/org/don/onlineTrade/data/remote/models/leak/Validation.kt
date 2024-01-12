package org.don.onlineTrade.data.remote.models.leak

data class Validation(
    val is_required: Boolean,
    val max: Any,
    val min: Any,
    val pattern: Any
)