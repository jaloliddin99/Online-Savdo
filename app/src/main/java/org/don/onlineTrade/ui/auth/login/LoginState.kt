package org.don.onlineTrade.ui.auth.login

import org.don.onlineTrade.data.remote.models.RegisterMain

data class LoginState(
    val isLoading: Boolean = false,
    val registerMain: RegisterMain?= null,
    val error: String = ""
)