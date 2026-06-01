package uz.promo.selling.ui.auth.verify

import uz.promo.selling.data.remote.models.VerificationRes


data class VerificationState(
    val isLoading: Boolean = false,
    val registerMain: VerificationRes?= null,
    val error: String = ""
)
