package org.don.onlineTrade.ui.auth.verify

import org.don.onlineTrade.data.remote.models.VerificationRes


data class VerificationState(
    val isLoading: Boolean = false,
    val registerMain: VerificationRes?= null,
    val error: String = ""
)
