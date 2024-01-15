package org.don.onlineTrade.ui.add

import org.don.onlineTrade.ui.auth.TextFieldState
import org.don.onlineTrade.ui.auth.textFieldStateSaver


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
    return "Please write at least 6 words for description."
}

private fun isValidDescription(description: String): Boolean {
    return countWords(description) > 6
}
fun countWords(input: String): Int {
    val words = input.trim().split("\\s+".toRegex())
    return words.size
}



class ProductPriceState(val price: String? = null) :
    TextFieldState(validator = ::isValidPrice, errorFor = ::priceValidationError) {
    init {
        price?.let {
            text = it
        }
    }
}



/**
 * Returns an error to be displayed or null if no error was found
 */
private fun priceValidationError(price: String): String {
    return "Enter the price"
}

private fun isValidPrice(title: String): Boolean {
    return title.isNotEmpty()
}







val ProductTitleStateSaver = textFieldStateSaver(ProductTitleState())
val ProductDescriptionStateSaver = textFieldStateSaver(ProductDescriptionState())
val ProductPriceStateSaver = textFieldStateSaver(ProductPriceState())