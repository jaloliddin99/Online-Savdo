package uz.don.onlineTrade.ui.detailsPage

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
fun DeletePostAlert(
    onDismiss: () -> Unit,
    onDeleteConfirm: () -> Unit
) {
    val configuration = LocalConfiguration.current

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        confirmButton = {
            Text(text = stringResource(id = com.google.android.material.R.string.abc_menu_delete_shortcut_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onDeleteConfirm() })
        },
        title = {
            Text(
                text = stringResource(id = R.string.delete_post),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.are_you_really_sure),
                fontSize = 14.sp,
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Normal
            )
        }
    )

}