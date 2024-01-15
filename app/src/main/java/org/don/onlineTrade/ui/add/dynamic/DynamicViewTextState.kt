package org.don.onlineTrade.ui.add.dynamic

import org.don.onlineTrade.ui.auth.TextFieldState


class DynamicViewState(
    private val txt: String? = null,
    private val regex: String?
) :
    TextFieldState(validator = {
        isValidText(it, regex)
    },
        errorFor = ::textValidationError) {
    init {
        txt?.let {
            text = it
        }
    }
}

private fun textValidationError(price: String): String {
    return "Enter the price"
}

private fun isValidText(input: String, regex: String?): Boolean {
    return if (regex?.isNotEmpty() == true)
        input.matches(Regex(regex))
    else
        return true
}

