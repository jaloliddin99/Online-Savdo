package org.don.onlineTrade.ui.auth.verify

import org.don.onlineTrade.data.remote.models.ModelSuccess
import org.don.onlineTrade.data.remote.models.RegisterMain


data class VerificationState(
    val isLoading: Boolean = false,
    val registerMain: ModelSuccess?= null,
    val error: String = ""
)
