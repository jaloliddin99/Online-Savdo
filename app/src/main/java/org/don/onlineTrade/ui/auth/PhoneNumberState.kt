package org.don.onlineTrade.ui.auth


import androidx.core.text.isDigitsOnly

class PhoneNumberState(val phone: String? = null) :
    TextFieldState(validator = ::isPhoneValid, errorFor = ::phoneNumberValidationError) {
    init {
        phone?.let {
            text = it
        }
    }
}
private fun phoneNumberValidationError(phone: String): String {
    return "Invalid phone: $phone"
}

private fun isPhoneValid(phone: String): Boolean {
    return phone.isDigitsOnly() && phone.length == 12
}










