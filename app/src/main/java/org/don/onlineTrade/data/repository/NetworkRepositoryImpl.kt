package org.don.onlineTrade.data.repository

import org.don.onlineTrade.data.remote.ApiInterface
import org.don.onlineTrade.data.remote.models.RegisterMain
import org.don.onlineTrade.domain.repository.NetworkRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NetworkRepositoryImpl @Inject constructor(
    private val apiInterface: ApiInterface
) : NetworkRepository {
    override suspend fun register(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String,
        phoneNumber: String
    ): RegisterMain {
        return apiInterface.register(
            "application/json",
            name,
            email,
            password,
            passwordConfirmation,
            phoneNumber
        )
    }

    override suspend fun login(email: String, password: String): RegisterMain {
        return apiInterface.login(
            "application/json",
            email,
            password
        )
    }


}