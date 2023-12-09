package org.don.onlineTrade.data.remote.models

data class RegisterMain(
    val error:Boolean,
    val token: String?,
    val errors: Errors?,
    val message: String?,
    val expired_at: Int?,
    val user_id: Int?,
)

data class Errors(
    val email: List<String>?,
    val phone_number: List<String>?
)


data class RegistrationBody(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val phoneNumber: String
)