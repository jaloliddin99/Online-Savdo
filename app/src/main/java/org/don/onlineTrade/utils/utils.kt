package org.don.onlineTrade.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Locale


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

@RequiresApi(Build.VERSION_CODES.O)
val convertDate: (String) -> String = { date: String->
    val dateTime = LocalDateTime.parse(date)
    val day = dateTime.dayOfMonth
    val month = dateTime.monthValue
    val year = dateTime.year
    val isCurrentYear = Year.now().value == year
    val formatter = DateTimeFormatter.ofPattern("d MMMM" + if (isCurrentYear) "" else ", yyyy")
        .withLocale(Locale.ENGLISH)
    val formattedDate = dateTime.format(formatter)
    formattedDate
}