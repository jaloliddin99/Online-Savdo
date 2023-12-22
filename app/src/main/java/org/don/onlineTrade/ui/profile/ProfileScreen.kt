package org.don.onlineTrade.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNotifications
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.getProfile.User
import org.don.onlineTrade.ui.add.ProductTitle
import org.don.onlineTrade.ui.add.TextBold
import org.don.onlineTrade.ui.add.TextThin
import org.don.onlineTrade.ui.dialogs.settings.SettingsDialog
import org.don.onlineTrade.ui.dialogs.settings.UserEditableSettings
import org.don.onlineTrade.ui.home.GetProfileState
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading
import org.don.onlineTrade.utils.SharedPref
import org.don.onlineTrade.utils.appLanguageName
import org.don.onlineTrade.utils.reverseAppLanguageName

@Composable
fun ProfileRoute(
    modifier: Modifier = Modifier,
    toMyProducts: () -> Unit,
    toUpdateProfile: () -> Unit,
    toUpdatePassword: () -> Unit,
    refreshProfile: Boolean = false,
    toForgotPassword: (Boolean) -> Unit
) {
    val viewModel = hiltViewModel<ProfileViewModel>()
    val state = viewModel.state.value
    if (refreshProfile){
        viewModel.refresh()
    }
    ProfileScreen(modifier, state, toMyProducts, toUpdateProfile, toUpdatePassword, toForgotPassword)
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    state: GetProfileState,
    toMyProducts: () -> Unit,
    toUpdateProfile: () -> Unit,
    toUpdatePassword: () -> Unit,
    toForgotPassword: (Boolean) -> Unit
) {


    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen24Dp))
            RoundImage(user = state.getProfile?.data)
            Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen12Dp))

            if (state.getProfile != null) {
                val user = state.getProfile.data
                TextBold(title = "${user.firstName}, ${user.lastName}")
                user.phoneNumber?.let { ProductTitle(title = it) }
                Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen24Dp))
                AppLanguage()
                Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen8Dp))
                ProfileSettingsAndPosts(
                    toMyProducts
                )
                Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen8Dp))

                ProfileUpdatePasswordAndProfile(
                    updateProfile = toUpdateProfile,
                    updatePassword = toUpdatePassword
                )
                Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen8Dp))
                AboutAppAndContactWithUs()
                Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen8Dp))
                LogOut(
                    logOut = {

                    },
                    forgotPassword = { toForgotPassword(false) }
                )
                Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen24Dp))

            }

        }
        FreeLoading(state.isLoading)
    }
}


@Composable
fun RoundImage(
    modifier: Modifier = Modifier,
    user: User?
) {
    var isLoading by remember {
        mutableStateOf(true)
    }
    var isError by remember {
        mutableStateOf(false)
    }
    val url = "http://91.227.40.169:8080/api/v1/user/image/${user?.profileUrl}"
    val imageLoader = rememberAsyncImagePainter(model = url,
        onState = { state ->
            isLoading = state is AsyncImagePainter.State.Loading
            isError = state is AsyncImagePainter.State.Error
        })
    Box(
        modifier = modifier
            .width(100.dp)
            .height(100.dp),
    ) {

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(50.dp),
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
        Image(
            painter = if (isError.not()) imageLoader else painterResource(id = R.drawable.user),
            contentDescription = null,
            modifier = modifier
                .fillMaxSize()
                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = CircleShape
                )
                .padding(3.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )

        Image(
            imageVector = Icons.Filled.Edit,
            contentDescription = null,
            modifier = modifier
                .height(37.dp)
                .width(37.dp)
                .padding(4.dp)
                .align(Alignment.BottomEnd)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(4.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
        )
    }
}


@Composable
fun AboutAppAndContactWithUs() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        ProfileColumnItem(
            imageVector = Icons.Filled.Warning,
            title = stringResource(id = R.string.about_app),
            onItemClicked = {

            }
        )
        Divider(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clip(CircleShape),
            thickness = 0.5.dp
        )
        ProfileColumnItem(
            imageVector = Icons.Filled.Help,
            title = stringResource(id = R.string.technical_assistance),
            onItemClicked = {

            }
        )
    }
}

@Composable
fun ProfileUpdatePasswordAndProfile(
    updateProfile: () -> Unit,
    updatePassword: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        ProfileColumnItem(
            imageVector = Icons.Filled.Settings,
            title = stringResource(id = R.string.update_profile),
            onItemClicked = updateProfile
        )
        Divider(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clip(CircleShape),
            thickness = 0.5.dp
        )
        ProfileColumnItem(
            imageVector = Icons.Filled.Password,
            title = stringResource(id = R.string.password_update),
            onItemClicked = updatePassword
        )
    }
}
@Composable
fun ProfileSettingsAndPosts(
    toMyProducts: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        ProfileColumnItem(
            imageVector = Icons.Filled.Settings,
            title = stringResource(id = R.string.profile_settings),
            onItemClicked = {

            }
        )
        Divider(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clip(CircleShape),
            thickness = 0.5.dp
        )
        ProfileColumnItem(
            imageVector = Icons.Filled.AddToPhotos,
            title = stringResource(id = R.string.my_orders),
            onItemClicked = toMyProducts
        )
    }
}

@Composable
fun LogOut(
    logOut: () -> Unit,
    forgotPassword: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        ProfileColumnItem(
            imageVector = Icons.Filled.Password,
            title = stringResource(id = R.string.forgot_password),
            onItemClicked = forgotPassword
        )
        Divider(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clip(CircleShape),
            thickness = 0.5.dp
        )
        ProfileColumnItem(
            imageVector = Icons.Filled.Logout,
            title = stringResource(id = R.string.logout),
            onItemClicked = logOut
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLanguage() {

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    ProfileColumnItem(
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_flag_uzb),
        title = stringResource(id = R.string.app_language),
        desc = appLanguageName(SharedPref.language),
        onItemClicked = {
            showBottomSheet = true
        }
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            RadioGroupExample(
                onLanguageSelected = {

                }
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
            Button(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = MaterialTheme.spacing.dimen16Dp)
            ) {
                Text(
                    text = stringResource(id = R.string.continuee),
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen24Dp))
        }
    }
}

@Composable
fun RadioGroupExample(
    onLanguageSelected: (String) -> Unit
) {
    val options = listOf("O'zbekcha", "Русский", "English")
    val selectedOption = remember { mutableStateOf(options.first()) }

    Column() {
        options.forEach { option ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (option == selectedOption.value),
                        onClick = { selectedOption.value = option }
                    )
                    .padding(8.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (option == selectedOption.value),
                    onClick = {
                        SharedPref.language = reverseAppLanguageName(selectedOption.value)
                        onLanguageSelected(SharedPref.language)
                    }
                )
                Text(
                    text = option,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
fun ProfileColumnItem(
    imageVector: ImageVector,
    title: String,
    desc: String = "",
    onItemClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onItemClicked() }
            .fillMaxWidth()
            .height(56.dp)
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(horizontal = MaterialTheme.spacing.dimen16Dp)
        ,
        verticalAlignment = Alignment.CenterVertically) {
        Image(
            modifier = Modifier
                .width(20.dp)
                .height(20.dp)
                .clip(CircleShape),
            imageVector = imageVector, contentDescription = null,
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen12Dp))
        TextThin(title = title)
        Spacer(modifier = Modifier.weight(1f))
        ProductTitle(title = desc)
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen12Dp))
        Image(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = null)
    }
}
