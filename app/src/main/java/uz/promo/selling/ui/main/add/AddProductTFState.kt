package uz.promo.selling.ui.main.add

import uz.promo.selling.ui.auth.TextFieldState
import uz.promo.selling.ui.auth.textFieldStateSaver
import uz.promo.selling.utils.tr


class ProductTitleState(val title: String? = null) :
    TextFieldState(validator = ::isValidTitle, errorFor = ::titleValidationError) {
    init {
        title?.let {
            text = it
        }
    }
}


/**
 * Returns an error to be displayed or null if no error was found
 */
private fun titleValidationError(title: String): String {
    return tr(
        en = "Title must be longer than 3 characters",
        ru = "Название должно быть длиннее 3 символов",
        uz = "Sarlavha 3 ta belgidan uzun bo'lishi kerak"
    )
}

private fun isValidTitle(title: String): Boolean {
    return title.length > 3
}



class ProductDescriptionState(val description: String? = null) :
    TextFieldState(validator = ::isValidDescription, errorFor = ::descriptionValidationError) {
    init {
        description?.let {
            text = it
        }
    }
}


/**
 * Returns an error to be displayed or null if no error was found
 */
private fun descriptionValidationError(description: String): String {
    return tr(
        en = "At least 10 characters, you have ${description.length}",
        ru = "Минимум 10 символов, у вас ${description.length}",
        uz = "Kamida 10 ta belgi, sizda ${description.length} ta"
    )
}

private fun isValidDescription(description: String): Boolean {
    return description.length > 10
}

class PostAddressState(val address: String? = null) :
    TextFieldState(validator = ::isValidLocation, errorFor = ::addressValidationError) {
    init {
        address?.let {
            text = it
        }
    }
}


/**
 * Returns an error to be displayed or null if no error was found
 */
private fun addressValidationError(title: String): String {
    return tr(
        en = "Location must be longer than 3 characters",
        ru = "Адрес должен быть длиннее 3 символов",
        uz = "Manzil 3 ta belgidan uzun bo'lishi kerak"
    )
}

private fun isValidLocation(title: String): Boolean {
    return title.length > 3
}


val ProductTitleStateSaver = textFieldStateSaver(ProductTitleState())
val ProductDescriptionStateSaver = textFieldStateSaver(ProductDescriptionState())
