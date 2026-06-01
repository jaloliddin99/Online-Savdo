package uz.promo.selling.utils


import android.util.Log
import uz.promo.selling.BuildConfig

object Logger {

    private const val GLOBAL_TAG = "selling_log" // You can customize this

    /** Whether logs should be shown. Usually `BuildConfig.DEBUG` */
    private val isLoggingEnabled = BuildConfig.DEBUG

    private fun getTag(customTag: String?): String {
        return if (!customTag.isNullOrBlank()) {
            "$GLOBAL_TAG-$customTag"
        } else {
            // Automatically infer class name from stack trace
            val stackTrace = Thread.currentThread().stackTrace
            val element = stackTrace.getOrNull(5)
            "$GLOBAL_TAG-${element?.fileName?.substringBefore(".") ?: "Unknown"}"
        }
    }

    fun d(message: String, customTag: String? = null) {
        if (isLoggingEnabled) Log.d(getTag(customTag), message)
    }

    fun i(message: String, customTag: String? = null) {
        if (isLoggingEnabled) Log.i(getTag(customTag), message)
    }

    fun w(message: String, customTag: String? = null) {
        if (isLoggingEnabled) Log.w(getTag(customTag), message)
    }

    fun e(message: String, throwable: Throwable? = null, customTag: String? = null) {
        if (isLoggingEnabled) Log.e(getTag(customTag), message, throwable)
    }

    fun v(message: String, customTag: String? = null) {
        if (isLoggingEnabled) Log.v(getTag(customTag), message)
    }

    /** For logging exceptions more safely */
    fun exception(throwable: Throwable, customTag: String? = null) {
        if (isLoggingEnabled) Log.e(getTag(customTag), throwable.message, throwable)
    }
}
