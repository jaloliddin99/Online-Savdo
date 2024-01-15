package org.don.onlineTrade.ui.add.dynamic

import androidx.core.text.isDigitsOnly
import org.don.onlineTrade.data.remote.models.leak.Validation
import org.don.onlineTrade.ui.auth.TextFieldState
import org.don.onlineTrade.utils.SharedPref


class DynamicViewState(
    private val txt: String? = null,
    private val regex: Validation
) :
    TextFieldState(validator = {
        isValidText(it, regex)
    },
        errorFor = {
            textValidationError(it, regex)
        }) {
    init {
        txt?.let {
            text = it
        }
    }
}

private fun textValidationError(price: String, validation: Validation): String {
    return translator(SharedPref.language, validation)
}

private fun translator(lang: String, validation: Validation): String {
    if (validation.max != null && validation.min != null) {
        return when (lang) {
            "ru" -> "Введите номер от ${validation.min} до ${validation.max}"
            "en" -> "Enter the number from ${validation.min} to ${validation.min}"
            else -> "${validation.min} dan ${validation.max} gacha kiriting"
        }
    }
    return "Wrong input"
}

private fun isValidText(input: String, validation: Validation): Boolean {
    if (validation.pattern?.isNotEmpty() == true) {
        val isMatched: Boolean = input.matches(Regex(validation.pattern))
        if (!isMatched)
            return false

        if (validation.max != null && validation.min != null) {
            if (!input.isDigitsOnly())
                return false
            if (input.toInt() >= validation.min && input.toInt() <= validation.max)
                return true
        }else
            return true

    }

    return false

}

