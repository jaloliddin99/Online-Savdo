package uz.promo.selling.ui.premium

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.WorkspacePremium
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.payments.PremiumPlan
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.utils.formatNumberWithSpaces
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val PremiumGold = Color(0xFFCBA135)

@Composable
fun PremiumRoute(
    onBack: () -> Unit,
    viewModel: PremiumViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val plans = viewModel.plans
    var selected by remember(plans) { mutableStateOf(plans.firstOrNull()?.termMonths) }
    val orderError = stringResource(R.string.payment_failed)

    fun pay(provider: String) {
        val term = selected ?: return
        viewModel.createOrder(term, provider) { url ->
            if (url != null) {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch (_: Exception) {
                    Toast.makeText(context, orderError, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, orderError, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val primary = MaterialTheme.colorScheme.primary

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(primary, primary.copy(alpha = 0.82f)))
                )
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
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WorkspacePremium,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Feature list
            FeatureRow(stringResource(R.string.premium_feature_ai))
            FeatureRow(stringResource(R.string.premium_feature_boosts))
            FeatureRow(stringResource(R.string.premium_feature_badge))
            FeatureRow(stringResource(R.string.premium_feature_analytics))
            FeatureRow(stringResource(R.string.premium_feature_interested))
            FeatureRow(stringResource(R.string.premium_feature_contact))

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.premium_choose_plan),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (viewModel.isLoading && plans.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val baseMonthly = plans.firstOrNull { it.termMonths == 1 }?.price
                plans.forEach { plan ->
                    PlanCard(
                        plan = plan,
                        baseMonthlyPrice = baseMonthly,
                        selected = selected == plan.termMonths,
                        onClick = { selected = plan.termMonths }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payme — logo
                PayButton(
                    background = Color(0xFF33CCCC),
                    enabled = !viewModel.isOrdering && selected != null,
                    onClick = { pay("payme") }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pay_me_logo),
                        contentDescription = stringResource(R.string.pay_with_payme),
                        modifier = Modifier.height(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                // Click — wordmark
                PayButton(
                    background = Color(0xFF0073E6),
                    enabled = !viewModel.isOrdering && selected != null,
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
                Spacer(modifier = Modifier.height(28.dp))
            }
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
            .background(Color.White.copy(alpha = 0.18f))
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
private fun FeatureRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
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
    selected: Boolean,
    onClick: () -> Unit,
) {
    val border = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    val perMonth = plan.price / plan.termMonths
    // Savings vs paying the 1-month price every month.
    val savePercent = baseMonthlyPrice?.takeIf { it > 0 && plan.termMonths > 1 }?.let {
        (100 - (perMonth * 100 / it)).toInt().coerceAtLeast(0)
    }?.takeIf { it > 0 }

    Row(
        modifier = Modifier
            .fillMaxWidth()
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
        Text(
            text = "${formatNumberWithSpaces(plan.price.toString())} ${stringResource(R.string.uzs)}",
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
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
