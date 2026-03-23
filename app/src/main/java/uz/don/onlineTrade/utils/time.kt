package uz.don.onlineTrade.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertLongToDateString(timeInMillis: Long): String {
    val date = Date(timeInMillis)
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
    return format.format(date)
}

fun convertLongToDisplayDate(timeInMillis: Long): String {
    val date = Date(timeInMillis)
    val format = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    return format.format(date)
}
