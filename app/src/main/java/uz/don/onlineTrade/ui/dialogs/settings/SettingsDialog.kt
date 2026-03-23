package uz.don.onlineTrade.ui.dialogs.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import uz.don.onlineTrade.R
import uz.don.onlineTrade.domain.model.DarkThemeConfig
import uz.don.onlineTrade.domain.model.ThemeBrand
import uz.don.onlineTrade.ui.theme.supportsDynamicTheming

const val SETTINGS_UI_STATE = "settings_ui_state"

@Composable
fun SettingsDialog(
    state: UserEditableSettings,
    onDismiss: () -> Unit,
    viewModel: SettingsDialogViewModel = hiltViewModel()
) {

    SettingsDialog(
        onDismiss = onDismiss,
        settingsUiState = state,
        onChangeThemeBrand = viewModel::updateThemeBrand,
        onChangeDynamicColorPreference = viewModel::updateDynamicColorPreference,
        onChangeDarkThemeConfig = viewModel::updateDarkThemeConfig,
    )

}

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    settingsUiState: UserEditableSettings,
    supportDynamicColor: Boolean = supportsDynamicTheming(),
    onChangeThemeBrand: (themeBrand: ThemeBrand) -> Unit,
    onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
) {

    val configuration = LocalConfiguration.current

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        title = {
            Text(
                text = stringResource(id = R.string.settings),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                SettingsPanel(
                    settings = settingsUiState,
                    supportDynamicColor = supportDynamicColor,
                    onChangeThemeBrand = onChangeThemeBrand,
                    onChangeDynamicColorPreference = onChangeDynamicColorPreference,
                    onChangeDarkThemeConfig = onChangeDarkThemeConfig,
                )
//                Divider(Modifier.padding(top = 8.dp))
//                LinksPanel()
            }
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            Text(text = stringResource(id = R.string.ok),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onDismiss() })
        },
    )

}


@Composable
private fun ColumnScope.SettingsPanel(
    settings: UserEditableSettings,
    supportDynamicColor: Boolean,
    onChangeThemeBrand: (themeBrand: ThemeBrand) -> Unit,
    onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
) {
    SettingsDialogSectionTitle(text = stringResource(id = R.string.theme))
    Column(Modifier.selectableGroup()) {
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.brand_default),
            selected = settings.brand == ThemeBrand.DEFAULT,
            onClick = {
                onChangeThemeBrand(ThemeBrand.DEFAULT)
            },
        )
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.brand_android),
            selected = settings.brand == ThemeBrand.ANDROID,
            onClick = {
                onChangeThemeBrand(ThemeBrand.ANDROID)
            },
        )
    }

    AnimatedVisibility(visible = settings.brand == ThemeBrand.DEFAULT && supportDynamicColor) {
        Column {
            SettingsDialogSectionTitle(text = stringResource(id = R.string.use_dynamic_color))
            Column(Modifier.selectableGroup()) {
                SettingsDialogThemeChooserRow(
                    text = stringResource(R.string.yes),
                    selected = settings.useDynamicColor,
                    onClick = {
                        onChangeDynamicColorPreference(true)
                    },
                )
                SettingsDialogThemeChooserRow(
                    text = stringResource(R.string.no),
                    selected = !settings.useDynamicColor,
                    onClick = {
                        onChangeDynamicColorPreference(false)
                    },
                )
            }
        }
    }

    SettingsDialogSectionTitle(text = stringResource(id = R.string.dark_mode_preference))
    Column(Modifier.selectableGroup()) {
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.system_default),
            selected = settings.darkThemeConfig == DarkThemeConfig.FOLLOW_SYSTEM,
            onClick = { onChangeDarkThemeConfig(DarkThemeConfig.FOLLOW_SYSTEM) },
        )
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.light),
            selected = settings.darkThemeConfig == DarkThemeConfig.LIGHT,
            onClick = { onChangeDarkThemeConfig(DarkThemeConfig.LIGHT) },
        )
        SettingsDialogThemeChooserRow(
            text = stringResource(R.string.Dark),
            selected = settings.darkThemeConfig == DarkThemeConfig.DARK,
            onClick = { onChangeDarkThemeConfig(DarkThemeConfig.DARK) },
        )
    }
}

@Composable
fun SettingsDialogThemeChooserRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected, role = Role.RadioButton,
                onClick = onClick
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
private fun SettingsDialogSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
    )
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LinksPanel() {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterHorizontally,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        val uriHandler = LocalUriHandler.current
        TextButton(
            onClick = { uriHandler.openUri("https://google.com") },
        ) {
            Text(text = stringResource(R.string.privacy_policy))
        }
        val context = LocalContext.current
        TextButton(
            onClick = {
                //context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            },
        ) {
            Text(text = stringResource(R.string.licenses))
        }
        TextButton(
            onClick = { uriHandler.openUri("https://google.com") },
        ) {
            Text(text = stringResource(R.string.brand_guidelines))
        }
        TextButton(
            onClick = { uriHandler.openUri("https://google.com") },
        ) {
            Text(text = stringResource(R.string.feedback))
        }
    }
}