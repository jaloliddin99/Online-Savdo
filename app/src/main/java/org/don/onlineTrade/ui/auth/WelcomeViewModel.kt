package org.don.onlineTrade.ui.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(): ViewModel() {

    fun handleContinue(
        email: String,
        onNavigateToSignIn: (email: String) -> Unit,
        onNavigateToSignUp: (email: String) -> Unit,
    ) {
        onNavigateToSignIn(email)

    }

    fun signInAsGuest(
        onSignInComplete: () -> Unit,
    ) {
        onSignInComplete()
    }
}