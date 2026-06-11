package uz.promo.selling.utils

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

/** Clock time (HH:mm) from a backend ISO local date-time, e.g. "2026-06-10T14:05:33.12". */
fun chatTimeLabel(iso: String?): String {
    if (iso.isNullOrBlank()) return ""
    val time = iso.substringAfter('T', "")
    return if (time.length >= 5) time.substring(0, 5) else ""
}

/** Conversation-list stamp: clock time if today, otherwise the date. */
fun chatListTimeLabel(iso: String?): String {
    if (iso.isNullOrBlank()) return ""
    val datePart = iso.substringBefore('T')
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date())
    return if (datePart == today) chatTimeLabel(iso) else datePart
}
