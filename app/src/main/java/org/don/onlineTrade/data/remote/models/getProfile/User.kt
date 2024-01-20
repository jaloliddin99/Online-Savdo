package org.don.onlineTrade.data.remote.models.getProfile

data class User(
    val email: String,
    val id: Int,
    val name: String,
    val role: String,
    val phoneNumber: String,
    val profileUrl: String
)