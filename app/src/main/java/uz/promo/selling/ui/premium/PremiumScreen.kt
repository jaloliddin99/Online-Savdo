package uz.promo.selling.ui.premium

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.payments.PremiumPlan
import uz.promo.selling.ui.theme.PremiumGold
import uz.promo.selling.ui.theme.PremiumGoldDark
import uz.promo.selling.ui.theme.PremiumGoldLight
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.utils.formatNumberWithSpaces
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Dark hero backdrop — deliberately darker than the everyday brand green so the
// paywall reads as its own premium space in both light and dark themes.
private val HeroTop = Color(0xFF0C2317)
private val HeroBottom = Color(0xFF17422A)

@Composable
fun PremiumRoute(
    onBack: () -> Unit,
    viewModel: PremiumViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val plans = viewModel.plans
    var selected by remember(plans) { mutableStateOf(plans.firstOrNull()?.termMonths) }
    // Members aren't pushed to buy again — tariffs stay hidden until they
    // explicitly ask to extend.
    var showExtendOptions by remember { mutableStateOf(false) }
    val orderError = stringResource(R.string.payment_failed)
    val activatedMsg = stringResource(R.string.premium_activated)

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
            icon = Icons.Rounded.Verified,
            iconTint = PremiumGold,
            title = activatedMsg,
            message = stringResource(R.string.premium_activated_desc),
            buttonText = stringResource(R.string.dialog_great),
            onDismiss = { viewModel.consumePaymentConfirmed() }
        )
    }

    fun pay(provider: String) {
        val term = selected ?: return
        viewModel.createOrder(term, provider) { url, errorMsg, free ->
            when {
                // 100% promo: already granted, so skip the checkout browser and let
                // the view model's polling surface the usual confirmation.
                free -> Unit
                url != null -> try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch (_: Exception) {
                    Toast.makeText(context, orderError, Toast.LENGTH_SHORT).show()
                }
                else -> Toast.makeText(context, errorMsg ?: orderError, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // The whole screen scrolls — the tall hero would otherwise squeeze the
    // plans on small phones.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            // enableEdgeToEdge() means this screen owns its insets: keep the pay
            // buttons clear of the gesture bar and the promo field above the keyboard.
            .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(HeroTop, HeroBottom)))
                .statusBarsPadding()
        ) {
            IconButton(onClick = onBack, modifier = Modifier.padding(4.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp, bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CrownHero()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.premium_title),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = Color.White
                )
                Text(
                    text = stringResource(R.string.premium_tagline),
                    fontFamily = robotoFontFamily,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
                if (viewModel.isPremium) {
                    Spacer(modifier = Modifier.height(10.dp))
                    ActiveStatusChip(
                        until = viewModel.premiumUntil,
                        credits = viewModel.boostCredits
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Feature list — each benefit with its own icon.
            FeatureRow(Icons.Rounded.AutoAwesome, stringResource(R.string.premium_feature_ai))
            FeatureRow(Icons.Rounded.RocketLaunch, stringResource(R.string.premium_feature_boosts))
            FeatureRow(Icons.Rounded.Verified, stringResource(R.string.premium_feature_badge))
            FeatureRow(Icons.Rounded.Insights, stringResource(R.string.premium_feature_analytics))
            FeatureRow(Icons.Rounded.Visibility, stringResource(R.string.premium_feature_interested))
            FeatureRow(Icons.Rounded.SupportAgent, stringResource(R.string.premium_feature_contact))

            Spacer(modifier = Modifier.height(24.dp))

            // A payment is in flight (user is in / just came back from the
            // checkout app) — shown regardless of the member gate below.
            if (viewModel.awaitingPayment) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PremiumGold.copy(alpha = 0.10f))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = PremiumGold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.payment_waiting),
                        fontFamily = robotoFontFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (viewModel.isPremium && !showExtendOptions) {
                // Active member: don't push tariffs at them — the plans appear
                // only when they explicitly want to extend the membership.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PremiumGold.copy(alpha = 0.14f))
                        .clickable { showExtendOptions = true }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.premium_extend),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = PremiumGoldDark
                    )
                }
                Spacer(modifier = Modifier.height(28.dp))
            } else {

            Text(
                text = stringResource(R.string.premium_choose_plan),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            // Members extending: make clear the term is ADDED, not replaced.
            if (viewModel.isPremium) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.premium_extend_note),
                    fontFamily = robotoFontFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (viewModel.isLoading && plans.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (plans.isEmpty()) {
                // No term has a configured price (admin hides premium by
                // pricing everything at 0) — say so instead of dead buttons.
                Text(
                    text = stringResource(R.string.premium_unavailable),
                    fontFamily = robotoFontFamily,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                val baseMonthly = plans.firstOrNull { it.termMonths == 1 }?.price
                plans.forEach { plan ->
                    PlanCard(
                        plan = plan,
                        baseMonthlyPrice = baseMonthly,
                        promoPercent = viewModel.promoPercent,
                        selected = selected == plan.termMonths,
                        onClick = { selected = plan.termMonths }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Promo code — validated against the currently selected term.
                uz.promo.selling.ui.PromoCodeRow(
                    appliedCode = viewModel.promoCode,
                    appliedPercent = viewModel.promoPercent,
                    checking = viewModel.promoChecking,
                    accent = PremiumGold,
                    onApply = { code ->
                        viewModel.applyPromo(code, selected) { msg ->
                            Toast.makeText(context, msg ?: orderError, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onClear = { viewModel.clearPromo() }
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Payme — logo
                PayButton(
                    background = Color(0xFF33CCCC),
                    enabled = !viewModel.isOrdering && !viewModel.awaitingPayment && selected != null,
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
                PayButton(
                    background = Color(0xFF0065FF),
                    enabled = !viewModel.isOrdering && !viewModel.awaitingPayment && selected != null,
                    onClick = { pay("click") }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.click_logo_white),
                        contentDescription = stringResource(R.string.pay_with_click),
                        modifier = Modifier.height(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(28.dp))
            }

            }
        }
    }
}

/** Gold crown inside a glowing ring with a gentle breathing animation. */
@Composable
private fun CrownHero() {
    val pulse by rememberInfiniteTransition(label = "crown")
        .animateFloat(
            initialValue = 1f,
            targetValue = 1.07f,
            animationSpec = infiniteRepeatable(
                animation = tween(1700, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "crownPulse"
        )
    Box(modifier = Modifier.size(116.dp), contentAlignment = Alignment.Center) {
        // Soft gold glow behind the crown.
        Box(
            modifier = Modifier
                .size(116.dp)
                .background(
                    Brush.radialGradient(
                        listOf(PremiumGold.copy(alpha = 0.35f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.dp, PremiumGold.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_premium_crown),
                contentDescription = null,
                modifier = Modifier
                    .size(46.dp)
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                    }
            )
        }
    }
}

@Composable
private fun ActiveStatusChip(until: String?, credits: Int) {
    val untilLabel = until?.let {
        runCatching {
            LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }.getOrNull()
    }
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.14f))
            .border(1.dp, PremiumGold.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (untilLabel != null) {
            Text(
                text = stringResource(R.string.premium_active_until, untilLabel),
                fontFamily = robotoFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
        Text(
            text = stringResource(R.string.premium_credits_left, credits),
            fontFamily = robotoFontFamily,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun FeatureRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PremiumGold.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PremiumGold,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontFamily = robotoFontFamily,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun PlanCard(
    plan: PremiumPlan,
    baseMonthlyPrice: Long?,
    promoPercent: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val isBestValue = plan.termMonths == 12
    val border by animateColorAsState(
        targetValue = when {
            selected -> MaterialTheme.colorScheme.primary
            isBestValue -> PremiumGold.copy(alpha = 0.55f)
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        },
        label = "planBorder"
    )
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.02f else 1f,
        label = "planScale"
    )
    val perMonth = plan.price / plan.termMonths
    // Savings vs paying the 1-month price every month.
    val savePercent = baseMonthlyPrice?.takeIf { it > 0 && plan.termMonths > 1 }?.let {
        (100 - (perMonth * 100 / it)).toInt().coerceAtLeast(0)
    }?.takeIf { it > 0 }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (isBestValue) 9.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
                    else MaterialTheme.colorScheme.surface
                )
                .border(if (selected) 2.dp else 1.dp, border, RoundedCornerShape(16.dp))
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .border(2.dp, border, CircleShape)
                    .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = termLabel(plan.termMonths),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (savePercent != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.premium_save, savePercent),
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(PremiumGold)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "${formatNumberWithSpaces(perMonth.toString())} ${stringResource(R.string.uzs)}${stringResource(R.string.premium_per_month)}",
                    fontFamily = robotoFontFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }
            if (promoPercent > 0) {
                uz.promo.selling.ui.PromoPriceText(
                    original = plan.price,
                    percent = promoPercent,
                    accent = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    horizontalAlignment = Alignment.End
                )
            } else {
                Text(
                    text = "${formatNumberWithSpaces(plan.price.toString())} ${stringResource(R.string.uzs)}",
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        if (isBestValue) {
            Text(
                text = stringResource(R.string.premium_best_value).uppercase(),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = Color(0xFF4A3908),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-16).dp, y = (-9).dp)
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(PremiumGoldLight, PremiumGold)))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun PayButton(
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

@Composable
private fun termLabel(months: Int): String = when (months) {
    1 -> stringResource(R.string.premium_month_1)
    3 -> stringResource(R.string.premium_months_3)
    12 -> stringResource(R.string.premium_months_12)
    else -> "$months"
}
