package org.don.onlineTrade.data.remote.models.getProfile

data class User(
    val email: String,
    val id: Int,
    val firstName: String,
    val lastName: String,
    val role: String,
    val phoneNumber: String,
    val profileUrl: String
)