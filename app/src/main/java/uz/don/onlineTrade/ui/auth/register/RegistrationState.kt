package uz.don.onlineTrade.ui.auth.register

import uz.don.onlineTrade.data.remote.models.ModelSuccess


data class RegistrationState(
    val isLoading: Boolean = false,
    val registerMain: ModelSuccess?= null,
    val error: String = "",
    val email: String = ""
)
