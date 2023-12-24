package org.don.onlineTrade.ui.auth


import android.util.Log

class PhoneNumberState(val phone: String? = "+998") :
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
    Log.d("TAG", "PhoneNumberdwadawdawdawdawdawd 000 $phone , ${phone.length}")
    return  phone.length == 13
}

fun removeFirstCharacterAndAllSpaces(input: String): String {
    return input.substring(1).replace("\\s".toRegex(), "")
}









