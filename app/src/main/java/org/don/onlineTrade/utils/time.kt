package org.don.onlineTrade.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertLongToDateString(timeInMillis: Long): String {
    val date = Date(timeInMillis)
    val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return format.format(date)
}