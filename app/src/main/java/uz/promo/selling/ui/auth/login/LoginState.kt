package uz.promo.selling.ui.auth.login

import uz.promo.selling.data.remote.models.ModelSuccess

data class LoginState(
    val isLoading: Boolean = false,
    val registerMain: ModelSuccess?= null,
    val error: String = "",
    val email: String = ""
)