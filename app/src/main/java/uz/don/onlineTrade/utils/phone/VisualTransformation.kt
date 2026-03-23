package uz.don.onlineTrade.utils.phone

import android.telephony.PhoneNumberUtils
import android.text.Selection
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.Locale


class PasswordVisualTransformation(val mask: Char = '\u2022') : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            text = AnnotatedString(mask.toString().repeat(text.text.length)),
            offsetMapping = object : OffsetMapping {

                override fun originalToTransformed(offset: Int): Int = offset

                override fun transformedToOriginal(offset: Int): Int = offset
            }
        )
    }
}
// Making XXXX-XXXX-XXXX-XXXX string.
val creditCardOffsetMapping = object : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        if (offset <= 3) return offset
        if (offset <= 7) return offset + 1
        if (offset <= 11) return offset + 2
        if (offset <= 16) return offset + 3
        return 19
    }

    override fun transformedToOriginal(offset: Int): Int {
        if (offset <= 4) return offset
        if (offset <= 9) return offset - 1
        if (offset <= 14) return offset - 2
        if (offset <= 19) return offset - 3
        return 16
    }
}
private val countryCode: String = Locale.getDefault().country
private val phoneNumberFormatter = PhoneNumberUtil.getInstance().getAsYouTypeFormatter(countryCode)

private fun getFormattedNumber(lastNonSeparator: Char, hasCursor: Boolean): String? {
    return if (hasCursor) {
        phoneNumberFormatter.inputDigitAndRememberPosition(lastNonSeparator)
    } else {
        phoneNumberFormatter.inputDigit(lastNonSeparator)
    }
}

class PhoneNumberVisualTransformation(
    countryCode: String = Locale.getDefault().country
) : VisualTransformation {

    private val phoneNumberFormatter = PhoneNumberUtil.getInstance().getAsYouTypeFormatter(countryCode)

    override fun filter(text: AnnotatedString): TransformedText {
        val transformation = reformat(text, Selection.getSelectionEnd(text))


        return TransformedText(
            AnnotatedString(transformation.formatted.orEmpty()),
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return transformation.originalToTransformed[offset.coerceIn(transformation.originalToTransformed.indices)]
                }
                override fun transformedToOriginal(offset: Int): Int {
                    return transformation.transformedToOriginal[offset.coerceIn(transformation.transformedToOriginal.indices)]
                }
            }
        )
    }

    private fun reformat(s: CharSequence, cursor: Int): Transformation {
        phoneNumberFormatter.clear()

        val curIndex = cursor - 1
        var formatted: String? = null
        var lastNonSeparator = 0.toChar()
        var hasCursor = false

        s.forEachIndexed { index, char ->
            if (PhoneNumberUtils.isNonSeparator(char)) {
                if (lastNonSeparator.code != 0) {
                    formatted = getFormattedNumber(lastNonSeparator, hasCursor)
                    hasCursor = false
                }
                lastNonSeparator = char
            }
            if (index == curIndex) {
                hasCursor = true
            }
        }

        if (lastNonSeparator.code != 0) {
            formatted = getFormattedNumber(lastNonSeparator, hasCursor)
        }
        val originalToTransformed = mutableListOf<Int>()
        val transformedToOriginal = mutableListOf<Int>()
        var specialCharsCount = 0
        formatted?.forEachIndexed { index, char ->
            if (!PhoneNumberUtils.isNonSeparator(char)) {
                specialCharsCount++
                transformedToOriginal.add(index - specialCharsCount)
            } else {
                originalToTransformed.add(index)
                transformedToOriginal.add(index - specialCharsCount)
            }
        }
        originalToTransformed.add(originalToTransformed.maxOrNull()?.plus(1) ?: 0)
        transformedToOriginal.add(transformedToOriginal.maxOrNull()?.plus(1) ?: 0)

        return Transformation(formatted, originalToTransformed, transformedToOriginal)
    }

    private fun getFormattedNumber(lastNonSeparator: Char, hasCursor: Boolean): String? {
        return if (hasCursor) {
            phoneNumberFormatter.inputDigitAndRememberPosition(lastNonSeparator)
        } else {
            phoneNumberFormatter.inputDigit(lastNonSeparator)
        }
    }

    private data class Transformation(
        val formatted: String?,
        val originalToTransformed: List<Int>,
        val transformedToOriginal: List<Int>
    )
}