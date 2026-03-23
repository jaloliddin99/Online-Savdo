package uz.don.selling.ui.auth.register

import uz.don.selling.data.remote.models.ModelSuccess


data class RegistrationState(
    val isLoading: Boolean = false,
    val registerMain: ModelSuccess?= null,
    val error: String = "",
    val email: String = ""
)
