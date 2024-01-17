package org.don.onlineTrade.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import org.don.onlineTrade.R
import java.time.LocalDateTime
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Locale

fun openSmsApp(context: Context, phoneNumber: String) {
    val smsUri = Uri.parse("smsto:$phoneNumber")
    val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri)
    smsIntent.putExtra("sms_body", "Hello, this is a pre-filled message.")
    context.startActivity(smsIntent)
}

fun callTo(phone: String = "", context: Context?) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
    context?.startActivity(intent)
}


val appLanguageName: (String) -> String = {
    when (it) {
        "uz" -> "O'zbekcha"
        "en" -> "English"
        "ru" -> "Русский"
        else -> "O'zbekcha"
    }
}

val appLanguageNameRes: (String) -> Int = {
    when (it) {
        "uz" -> R.drawable.ic_flag_uzb
        "en" -> R.drawable.ic_flag_gb
        "ru" -> R.drawable.ic_flag_russsian
        else -> R.drawable.ic_flag_uzb
    }
}

val reverseAppLanguageName: (String) -> String = {
    when (it) {
        "O'zbekcha" -> "uz"
        "English" -> "en"
        "Русский" -> "ru"
        else -> "uz"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
val convertDate: (String) -> String = { date: String ->
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

val LOCATION_REVERSE_URL = "https://geocode-maps.yandex.ru/1.x/?apikey=867ef81c-359f-4363-b8f5-c77949de3dbd&geocode="
