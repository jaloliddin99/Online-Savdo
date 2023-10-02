package org.don.onlineTrade.utils


val appLanguageName: (String) -> String = {
    when(it){
        "uz" -> "O'zbekcha"
        "en" -> "English"
        "ru" -> "Русский"
        else -> "O'zbekcha"
    }
}

val reverseAppLanguageName: (String) -> String = {
    when(it){
        "O'zbekcha" -> "uz"
        "English" -> "uz"
        "Русский" -> "uz"
        else -> "uz"
    }
}
