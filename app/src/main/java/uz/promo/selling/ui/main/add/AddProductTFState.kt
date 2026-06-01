package uz.promo.selling.ui.main.add

import uz.promo.selling.ui.auth.TextFieldState
import uz.promo.selling.ui.auth.textFieldStateSaver


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
    return "Title length must be greater than 3"
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
    return "At least 10 character, you have ${description.length}"
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
    return "Location length must be greater than 3"
}

private fun isValidLocation(title: String): Boolean {
    return title.length > 3
}


val ProductTitleStateSaver = textFieldStateSaver(ProductTitleState())
val ProductDescriptionStateSaver = textFieldStateSaver(ProductDescriptionState())
