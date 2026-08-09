package uz.promo.selling.ui.auth


import android.util.Log
import uz.promo.selling.utils.tr

class PhoneNumberState(val phone: String? = "+998") :
    TextFieldState(validator = ::isPhoneValid, errorFor = ::phoneNumberValidationError) {
    init {
        phone?.let {
            text = it
        }
    }
}
private fun phoneNumberValidationError(phone: String): String {
    return tr(
        en = "Invalid phone: $phone",
        ru = "Неверный номер телефона: $phone",
        uz = "Telefon raqami noto'g'ri: $phone"
    )
}

private fun isPhoneValid(phone: String): Boolean {
    Log.d("TAG", "PhoneNumberdwadawdawdawdawdawd 000 $phone , ${phone.length}")
    return  phone.length == 13
}

fun removeFirstCharacterAndAllSpaces(input: String): String {
    return input.substring(1).replace("\\s".toRegex(), "")
}









