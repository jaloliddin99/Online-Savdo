package uz.don.onlineTrade.ui.main.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import uz.don.onlineTrade.R
import uz.don.onlineTrade.ui.theme.robotoFontFamily

@Composable
fun AskLocationDialog(
    allowed:  (Boolean) -> Unit,
) {
    val configuration = LocalConfiguration.current

    AlertDialog(
        onDismissRequest = { allowed(false) },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        confirmButton = {
            Text(text = stringResource(id = R.string.allow),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable {
                        allowed(true)
                    })
        },
        title = {
            Text(
                text = stringResource(id = R.string.notice),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.give_your_location),
                fontSize = 16.sp,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal
            )
        }
    )

}