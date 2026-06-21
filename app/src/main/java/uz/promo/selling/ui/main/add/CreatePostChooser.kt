package uz.promo.selling.ui.main.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Entry chooser: AI-assisted posting vs the manual wizard. */
@Composable
fun CreatePostChooserView(
    onCreateWithAi: () -> Unit,
    onCreateManually: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "How do you want to post?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(20.dp))

        // ── Create with AI (gradient, primary) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF4285F4), Color(0xFF9B72F2), Color(0xFFEA4C89))
                    )
                )
                .clickable { onCreateWithAi() }
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "✨ Create with AI",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Add photos — we write the title, description, pick the category and fill the details for you.",
                    color = Color.White.copy(alpha = 0.92f)
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // ── Create manually (outlined, secondary) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                .clickable { onCreateManually() }
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "✍️ Create manually",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Fill in the category, title, description and details yourself, step by step.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** Temporary stand-in for the AI flow (Phase 2/3 replaces this). */
@Composable
fun AiCreatePlaceholder(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("✨ AI posting flow is coming here next.", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onBack) { Text("Back") }
    }
}
