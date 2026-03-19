package org.don.onlineTrade.data.remote.models


data class RegistrationBody(
    val email: String,
    val name: String,
    val password: String,
    val phoneNumber: String
)

data class LoginBody(
    val email: String,
    val password: String,
)

data class VerificationRes(
    val status: Boolean,
    val message: String,
    val token: String,
    val refreshToken: String? = null
)

data class ModelSuccess(
    val message: String,
    val success: Boolean
)

data class GenericModel<T>(
    val message: String,
    val success: Boolean,
    val `data`: T
)