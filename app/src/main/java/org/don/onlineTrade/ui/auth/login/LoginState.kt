package org.don.onlineTrade.ui.auth.login

import org.don.onlineTrade.data.remote.models.ModelSuccess

data class LoginState(
    val isLoading: Boolean = false,
    val registerMain: ModelSuccess?= null,
    val error: String = "",
    val email: String = ""
)