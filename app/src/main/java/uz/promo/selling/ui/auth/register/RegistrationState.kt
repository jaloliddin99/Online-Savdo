package uz.promo.selling.ui.auth.register

import uz.promo.selling.data.remote.models.ModelSuccess


data class RegistrationState(
    val isLoading: Boolean = false,
    val registerMain: ModelSuccess?= null,
    val error: String = "",
    val email: String = ""
)
