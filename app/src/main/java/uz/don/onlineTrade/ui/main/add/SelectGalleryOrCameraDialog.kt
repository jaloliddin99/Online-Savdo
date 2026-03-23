package uz.don.onlineTrade.ui.main.add

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import uz.don.onlineTrade.R
import uz.don.onlineTrade.ui.theme.spacing

@Composable
fun DialogCameraOrGallery(
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit,
    onDismiss: () -> Unit,
) {
    val configuration = LocalConfiguration.current

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        title = {
            Text(
                text = stringResource(id = R.string.select_options),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                CustomText(title = R.string.camera, onTextClicked = onCameraSelected)
                Divider(
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.dimen8Dp)
                )
                CustomText(title = R.string.gallery, onTextClicked = onGallerySelected)
            }
        },
        confirmButton = {

        },
        )
}

@Composable
fun CustomText(@StringRes title: Int,
               onTextClicked: () -> Unit) {
    Text(
        text = stringResource(id = title),
        fontWeight = FontWeight.Medium,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.clickable {
            onTextClicked()
        }
    )
}