package org.don.onlineTrade.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
)

val LocalSpacing = compositionLocalOf{ Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current
