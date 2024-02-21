package org.don.onlineTrade.ui.profile.myPosts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.don.onlineTrade.R
import org.don.onlineTrade.ui.theme.spacing
import kotlin.math.roundToInt

@Composable
fun PrioritizeDialogContent(
    onButtonClicked: (Int) -> Unit
) {
    val timeDurations = listOf(
        3L, 6L, 12L, 24L, // Hours for 1 day
        72L, 144L, 240L, 360L, 720L // Hours for multiple days, converted to hours
    )

    // Using a continuous range for the slider
    var sliderPosition by remember { mutableStateOf(0f) }
    val maxIndex = (timeDurations.size - 1).toFloat()

    // Map the continuous slider position to the nearest duration
    val nearestDurationIndex = mapSliderPositionToNearestDurationIndex(sliderPosition, maxIndex, timeDurations)
    val selectedDuration = timeDurations[nearestDurationIndex]

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(text = "Selected duration: ${formatDuration(selectedDuration)}")

        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it.coerceIn(0f, maxIndex) },
            valueRange = 0f..maxIndex,
            steps = 0 // Making the slider move smoothly without snapping to steps
        )

        Text(text = "Selected duration in hours: $selectedDuration hours")


        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                onButtonClicked.invoke(selectedDuration.toInt())
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = stringResource(id = R.string.select),
                style = MaterialTheme.typography.titleSmall
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen24Dp))

    }
}

fun mapSliderPositionToNearestDurationIndex(position: Float, maxIndex: Float, durations: List<Long>): Int {
    val index = (position / maxIndex * (durations.size - 1)).roundToInt()
    return index.coerceIn(durations.indices)
}

fun formatDuration(hours: Long): String {
    return when {
        hours < 24 -> "$hours hours"
        hours == 24L -> "1 day"
        else -> {
            val days = hours / 24
            "$days days"
        }
    }
}