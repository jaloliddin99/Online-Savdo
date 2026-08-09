package uz.promo.selling.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import uz.promo.selling.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDateTime
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Locale


fun formatNumberWithSpaces(numberStr: String?): String {
    if (numberStr == null)
        return ""
    val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ' '
    }
    val formatter = DecimalFormat("#,###", symbols)
    return formatter.format(numberStr.toLong())
}
/**
 * OLX-imported descriptions contain literal `<br />` tags. Turn them into real
 * line breaks so the text renders cleanly instead of showing the raw tag.
 */
fun stripHtmlBreaks(text: String): String =
    text.replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")

fun openSmsApp(context: Context, phoneNumber: String) {
    val smsUri = Uri.parse("smsto:$phoneNumber")
    val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri)
    smsIntent.putExtra("sms_body", context.getString(R.string.sms_prefill))
    context.startActivity(smsIntent)
}

/**
 * In-code translation for the few places that have no Context (TextFieldState
 * validators are plain functions bound at construction). Keyed off the same
 * SharedPref.language the rest of the app switches on.
 */
fun tr(en: String, ru: String, uz: String): String = when (SharedPref.language) {
    "ru" -> ru
    "en" -> en
    else -> uz
}

/**
 * ViewModels and use cases fall back to a small set of canonical English error
 * strings when a request fails locally (no response body to localize). Map
 * those to string resources at display time; anything else came from the
 * backend already localized via the lang param, so pass it through.
 */
fun localizedError(context: Context, raw: String?): String = when {
    raw.isNullOrEmpty() ||
    raw == "An unexpected error occured" ||
    raw == "An unexpected error occurred" ||
    raw == "Error occurred!" -> context.getString(R.string.error_unexpected)
    // retrofit2.HttpException messages are "HTTP <code> <reason>". Any 5xx
    // means the server itself is down/restarting (e.g. 502 during a deploy),
    // not a problem on the user's side.
    raw.startsWith("HTTP 5") -> context.getString(R.string.error_server_down)
    raw == "Couldn't reach server. Check your internet connection." ->
        context.getString(R.string.error_no_internet)
    raw == "AI couldn't generate a draft." -> context.getString(R.string.error_ai_draft)
    raw == "Google auth failed" || raw == "Unexpected credential type" ->
        context.getString(R.string.error_google_auth)
    raw == "Google sign-in cancelled" -> context.getString(R.string.error_google_cancelled)
    raw == "Failed to update profile" -> context.getString(R.string.error_profile_update)
    else -> raw
}

fun callTo(phone: String = "", context: Context?) {
    val intent = Intent(Intent.ACTION_DIAL,
        Uri.fromParts("tel",
            if (!phone.startsWith("+")) "+${phone}" else phone,
            null)
    )
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
    val formatter = DateTimeFormatter.ofPattern("d MMM" + if (isCurrentYear) "" else ", yyyy")
        .withLocale(Locale.getDefault())
    val formattedDate = dateTime.format(formatter)
    formattedDate
}

val LOCATION_REVERSE_URL = "https://geocode-maps.yandex.ru/1.x/?apikey=867ef81c-359f-4363-b8f5-c77949de3dbd&geocode="
