package uz.promo.selling.ui.main.myPosts

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.payments.BoostTariff
import uz.promo.selling.ui.TopAppBar
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.utils.FreeLoading
import uz.promo.selling.utils.formatNumberWithSpaces

/**
 * Full-screen "Promote to top" flow: pick a duration tier (any number the backend
 * sends), then pay via Payme or Click. The checkout opens in the browser; the
 * backend applies the boost once the provider confirms payment.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BoostRoute(
    postId: Long,
    onBack: () -> Unit,
    viewModel: MyPostViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val tariffs = viewModel.tariffs
    val busy = viewModel.state.value.isLoading
    val paymentFailed = stringResource(R.string.payment_failed)
    val promotedMsg = stringResource(R.string.boost_promoted_success)

    LaunchedEffect(Unit) {
        viewModel.loadTariffs()
        viewModel.loadBoostCredits()
    }

    // Restart pending-order polling on every resume — covers both returning from
    // the checkout app and process death while paying (id is persisted).
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.pollPendingOrder()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (viewModel.paymentConfirmed) {
        uz.promo.selling.ui.PaymentSuccessDialog(
            icon = Icons.Rounded.RocketLaunch,
            iconTint = MaterialTheme.colorScheme.primary,
            title = promotedMsg,
            message = stringResource(R.string.boost_promoted_desc),
            buttonText = stringResource(R.string.dialog_great),
            onDismiss = {
                viewModel.consumePaymentConfirmed()
                onBack()
            }
        )
    }

    var selectedHours by remember(tariffs) { mutableStateOf(tariffs.firstOrNull()?.hours) }

    fun pay(provider: String) {
        val hours = selectedHours ?: return
        viewModel.createBoostOrder(postId, hours, provider) { url ->
            if (url != null) {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch (_: Exception) {
                    Toast.makeText(context, paymentFailed, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, paymentFailed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = stringResource(R.string.boost_title),
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onBack,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Hero
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.boost_title),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.boost_subtitle),
                fontFamily = robotoFontFamily,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                modifier = Modifier.padding(top = 4.dp)
            )

            // A payment is in flight (user is in / just came back from the
            // checkout app) — confirmation lands via background polling.
            if (viewModel.awaitingPayment) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.payment_waiting),
                        fontFamily = robotoFontFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                }
            }

            // Premium members can promote for free using an included credit.
            if (viewModel.boostCredits > 0) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFCBA135).copy(alpha = 0.14f))
                        .clickable(enabled = !busy) {
                            viewModel.promoteWithCredit(postId) { ok ->
                                Toast.makeText(
                                    context,
                                    if (ok) promotedMsg else paymentFailed,
                                    Toast.LENGTH_SHORT
                                ).show()
                                if (ok) onBack()
                            }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = Color(0xFFB8860B),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.boost_use_credit, viewModel.boostCredits),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionLabel(stringResource(R.string.boost_select_duration))
            Spacer(modifier = Modifier.height(10.dp))

            if (tariffs.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    tariffs.forEach { tariff ->
                        DurationTile(
                            tariff = tariff,
                            selected = selectedHours == tariff.hours,
                            onClick = { selectedHours = tariff.hours }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
                SectionLabel(stringResource(R.string.boost_choose_payment))
                Spacer(modifier = Modifier.height(12.dp))

                // Payme — brand logo.
                PaymentButton(
                    background = Color(0xFF33CCCC),
                    enabled = !busy && selectedHours != null,
                    onClick = { pay("payme") }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pay_me_logo),
                        contentDescription = stringResource(R.string.pay_with_payme),
                        modifier = Modifier.height(22.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Click — styled wordmark in brand blue.
                PaymentButton(
                    background = Color(0xFF0073E6),
                    enabled = !busy && selectedHours != null,
                    onClick = { pay("click") }
                ) {
                    Text(
                        text = "Click",
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        text = " Up",
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF8FE3FF)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    FreeLoading(isFeedLoading = busy)
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontFamily = robotoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun DurationTile(
    tariff: BoostTariff,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
            .then(
                if (selected) Modifier else Modifier
            )
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formatDurationLabel(tariff.hours),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "${formatNumberWithSpaces(tariff.price.toString())} ${stringResource(R.string.uzs)}",
            fontFamily = robotoFontFamily,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun PaymentButton(
    background: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (enabled) background else background.copy(alpha = 0.4f))
            .clickable(enabled = enabled) { onClick() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

/** "24h" / "3 days" / "1 week"-style label from an hour count. */
@Composable
private fun formatDurationLabel(hours: Int): String = when {
    hours < 24 -> "$hours ${stringResource(R.string.hours_short)}"
    hours % (24 * 7) == 0 -> {
        val weeks = hours / (24 * 7)
        "$weeks ${stringResource(R.string.weeks_short)}"
    }
    hours % 24 == 0 -> {
        val days = hours / 24
        "$days ${stringResource(R.string.days_short)}"
    }
    else -> "$hours ${stringResource(R.string.hours_short)}"
}
