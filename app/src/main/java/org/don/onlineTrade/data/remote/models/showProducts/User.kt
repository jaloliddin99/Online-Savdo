package org.don.onlineTrade.data.remote.models.showProducts

import java.io.Serializable

data class User(
    val email: String,
    val id: Int,
    val image: Any,
    val name: String,
    val phone_number: String?
): Serializable