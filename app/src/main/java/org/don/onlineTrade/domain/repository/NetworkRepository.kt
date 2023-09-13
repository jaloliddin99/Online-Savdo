package org.don.onlineTrade.domain.repository

import kotlinx.coroutines.flow.Flow
import org.don.onlineTrade.data.remote.models.RegisterMain

interface NetworkRepository {


    suspend fun register(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String,
        phoneNumber: String
    ): RegisterMain

}