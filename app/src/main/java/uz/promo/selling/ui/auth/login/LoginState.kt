package uz.promo.selling.ui.auth.login

import uz.promo.selling.data.remote.models.VerificationRes

data class LoginState(
    val isLoading: Boolean = false,
    val registerMain: VerificationRes?= null,
    val error: String = "",
    val email: String = ""
)