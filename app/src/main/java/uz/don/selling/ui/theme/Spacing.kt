package uz.don.selling.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Spacing(
    val dimen0Dp: Dp = 0.dp,
    val dimen1Dp: Dp = 1.dp,
    val dimen2Dp: Dp = 2.dp,
    val dimen4Dp: Dp = 4.dp,
    val dimen6Dp: Dp = 6.dp,
    val dimen8Dp: Dp = 8.dp,
    val dimen10Dp: Dp = 10.dp,
    val dimen12Dp: Dp = 12.dp,
    val dimen16Dp: Dp = 16.dp,
    val dimen20Dp: Dp = 20.dp,
    val dimen24Dp: Dp = 24.dp,
    val dimen30Dp: Dp = 30.dp,
    val dimen32Dp: Dp = 32.dp,
    val dimen40Dp: Dp = 40.dp,
    val dimen80Dp: Dp = 80.dp,
    val dimen100Dp: Dp = 100.dp,
    val dimen120Dp: Dp = 120.dp,
    val dimen56Dp: Dp = 56.dp,


    val dimen0Sp: TextUnit = 0.sp,
    val dimen1Sp: TextUnit = 1.sp,
    val dimen2Sp: TextUnit = 2.sp,
    val dimen4Sp: TextUnit = 4.sp,
    val dimen6Sp: TextUnit = 6.sp,
    val dimen8Sp: TextUnit = 8.sp,
    val dimen10Sp: TextUnit = 10.sp,
    val dimen12Sp: TextUnit = 12.sp,
    val dimen16Sp: TextUnit = 16.sp,
    val dimen20Sp: TextUnit = 20.sp,
    val dimen24Sp: TextUnit = 24.sp,
    val dimen30Sp: TextUnit = 30.sp,
    val dimen32Sp: TextUnit = 32.sp,
    val dimen40Sp: TextUnit = 40.sp,
    val dimen80Sp: TextUnit = 80.sp,
    val dimen100Sp: TextUnit = 100.sp,
    val dimen120Sp: TextUnit = 120.sp,
)

val LocalSpacing = compositionLocalOf{ Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current



