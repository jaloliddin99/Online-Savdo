package org.don.onlineTrade.utils


val appLanguageName: (String) -> String = {
    when(it){
        "uz" -> "O'zbekcha"
        "en" -> "English"
        "ru" -> "Русский"
        else -> "O'zbekcha"
    }
}