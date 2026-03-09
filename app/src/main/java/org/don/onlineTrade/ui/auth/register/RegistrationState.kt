package org.don.onlineTrade.ui.auth.register

import org.don.onlineTrade.data.remote.models.ModelSuccess


data class RegistrationState(
    val isLoading: Boolean = false,
    val registerMain: ModelSuccess?= null,
    val error: String = "",
    val email: String = ""
)
