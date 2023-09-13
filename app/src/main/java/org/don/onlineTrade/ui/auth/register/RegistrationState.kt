package org.don.onlineTrade.ui.auth.register

import org.don.onlineTrade.data.remote.models.RegisterMain


data class RegistrationState(
    val isLoading: Boolean = false,
    val registerMain: RegisterMain ?= null,
    val error: String = ""
)
