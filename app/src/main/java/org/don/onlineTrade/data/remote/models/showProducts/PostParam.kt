package org.don.onlineTrade.data.remote.models.showProducts

data class PostParam(
    val code: String,
    val id: Int,
    val label: String,
    val param_unit: ParamUnit?,
    val post_value: List<PostValue>,
    val type: String
)