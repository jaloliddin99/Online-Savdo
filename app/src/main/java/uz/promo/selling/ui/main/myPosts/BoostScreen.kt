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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Schedule
import uz.promo.selling.ui.theme.PremiumGold
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
    // Members with free credits aren't pushed to pay — the paid options stay
    // hidden until they explicitly ask for them.
    var showPayOptions by remember { mutableStateOf(false) }

    fun pay(provider: String) {
        val hours = selectedHours ?: return
        viewModel.createBoostOrder(postId, hours, provider) { url, errorMsg, free ->
            when {
                // 100% promo: nothing to pay, so no browser trip. The view model
                // polls the (already paid) order and the usual confirmation shows.
                free -> Unit
                url != null -> try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch (_: Exception) {
                    Toast.makeText(context, paymentFailed, Toast.LENGTH_SHORT).show()
                }
                else -> Toast.makeText(context, errorMsg ?: paymentFailed, Toast.LENGTH_SHORT).show()
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
                .fillMaxWidth()
                // weight, not fillMaxSize: a fillMaxSize child of a Column takes the
                // Column's FULL height rather than what's left under the app bar, so
                // the scroll viewport ran off-screen and the last screenful was
                // unreachable — the "can't scroll" symptom.
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                // enableEdgeToEdge() means this screen owns its insets: keep the last
                // buttons clear of the gesture bar, and lift the promo field above the
                // keyboard. union (not two stacked paddings) so an open keyboard
                // doesn't add the nav bar height on top of the IME height.
                .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
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

            // Premium members promote for FREE with an included credit — that's
            // the primary action; paid options are collapsed below it.
            if (viewModel.boostCredits > 0) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PremiumGold)
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.boost_use_credit, viewModel.boostCredits),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }

            if (viewModel.boostCredits > 0 && !showPayOptions) {
                // Free credit is the offer — paying is opt-in.
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = stringResource(R.string.boost_or_pay),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showPayOptions = true }
                        .padding(vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
            } else {

            Spacer(modifier = Modifier.height(24.dp))
            SectionLabel(
                text = stringResource(R.string.boost_select_duration),
                icon = Icons.Rounded.Schedule
            )
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
                            promoPercent = viewModel.promoPercent,
                            selected = selectedHours == tariff.hours,
                            onClick = { selectedHours = tariff.hours }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
                SectionLabel(
                    text = stringResource(R.string.boost_choose_payment),
                    icon = Icons.Rounded.CreditCard
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Promo code — validated against the currently selected duration.
                uz.promo.selling.ui.PromoCodeRow(
                    appliedCode = viewModel.promoCode,
                    appliedPercent = viewModel.promoPercent,
                    checking = viewModel.promoChecking,
                    accent = MaterialTheme.colorScheme.primary,
                    onApply = { code ->
                        viewModel.applyPromo(code, selectedHours) { msg ->
                            Toast.makeText(context, msg ?: paymentFailed, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onClear = { viewModel.clearPromo() }
                )
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

                // Click — brand logo on Click blue.
                PaymentButton(
                    background = Color(0xFF0065FF),
                    enabled = !busy && selectedHours != null,
                    onClick = { pay("click") }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.click_logo_white),
                        contentDescription = stringResource(R.string.pay_with_click),
                        modifier = Modifier.height(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            }
        }
    }

    FreeLoading(isFeedLoading = busy)
}

@Composable
private fun SectionLabel(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Icon in a tinted rounded box — same chip style as the premium
        // screen's feature list.
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
        Text(
            text = text,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DurationTile(
    tariff: BoostTariff,
    promoPercent: Int,
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
        if (promoPercent > 0) {
            uz.promo.selling.ui.PromoPriceText(
                original = tariff.price,
                percent = promoPercent,
                accent = MaterialTheme.colorScheme.primary
            )
        } else {
            Text(
                text = "${formatNumberWithSpaces(tariff.price.toString())} ${stringResource(R.string.uzs)}",
                fontFamily = robotoFontFamily,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
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
