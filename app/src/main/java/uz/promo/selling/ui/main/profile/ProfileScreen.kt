package uz.promo.selling.ui.main.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import uz.promo.selling.BuildConfig
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.getProfile.User
import uz.promo.selling.ui.TopAppBar
import uz.promo.selling.ui.main.add.DialogCameraOrGallery
import uz.promo.selling.ui.main.add.ImageUrl
import uz.promo.selling.ui.main.add.TextBold16
import uz.promo.selling.ui.main.add.TextNormal16
import uz.promo.selling.ui.main.add.TextThin
import uz.promo.selling.ui.main.home.GetProfileState
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.ui.theme.spacing
import uz.promo.selling.utils.ComposeFileProvider
import uz.promo.selling.utils.FreeLoading
import uz.promo.selling.utils.LocaleManager
import uz.promo.selling.utils.SharedPref
import uz.promo.selling.utils.appLanguageName
import uz.promo.selling.utils.appLanguageNameRes
import uz.promo.selling.utils.reverseAppLanguageName
import uz.promo.selling.utils.runTimePermission.RunTimePermission

@Composable
fun ProfileRoute(
    modifier: Modifier = Modifier,
    toMyProducts: () -> Unit,
    toUpdateProfile: () -> Unit,
    toUpdatePassword: () -> Unit,
    refreshProfile: Boolean = false,
    toForgotPassword: (Boolean) -> Unit,
    goToRegistration: () -> Unit,
    restartApp: () -> Unit,
    toNotifications: () -> Unit = {},
    toNotificationSettings: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val viewModel = hiltViewModel<ProfileViewModel>()
    val state = viewModel.state.value
    LaunchedEffect(refreshProfile) {
        if (refreshProfile) {
            viewModel.refresh()
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = stringResource(R.string.profile),
            actionIcon = Icons.Filled.Settings,
            onActionClick = onSettingsClick,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
        ProfileScreen(
            modifier = modifier.weight(1f),
            state = state,
            toMyProducts = toMyProducts,
            toUpdateProfile = toUpdateProfile,
            toUpdatePassword = toUpdatePassword,
            toForgotPassword = toForgotPassword,
            goToRegistration = goToRegistration,
            uploadImage = {
                viewModel.updateProfileImage(it)
            },
            restartApp = restartApp,
            toNotifications = toNotifications,
            toNotificationSettings = toNotificationSettings
        )
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    state: GetProfileState,
    toMyProducts: () -> Unit,
    toUpdateProfile: () -> Unit,
    toUpdatePassword: () -> Unit,
    toForgotPassword: (Boolean) -> Unit,
    goToRegistration: () -> Unit,
    uploadImage: (ImageUrl) -> Unit,
    restartApp: () -> Unit,
    toNotifications: () -> Unit = {},
    toNotificationSettings: () -> Unit = {}
) {
    val context = LocalContext.current

    var showGalleryOrCameraDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }


    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { uploadImage(ImageUrl(isFromCamera = true, uri, uri)) }
        }


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            uploadImage(ImageUrl(isFromCamera = true, capturedImageUri, capturedImageUri))
        }
    )

    if (showGalleryOrCameraDialog) {
        DialogCameraOrGallery(onDismiss = {
            showGalleryOrCameraDialog = false
        }, onCameraSelected = {
            RunTimePermission().permissionListForCamera(
                cameraPermission = {
                    if (it) {
                        val uri = ComposeFileProvider.getImageUri(context)
                        capturedImageUri = uri
                        cameraLauncher.launch(uri)
                    }
                }, context
            )
            showGalleryOrCameraDialog = false
        }, onGallerySelected = {
            RunTimePermission().permissionForGallery(
                galleryPermission = {
                    if (it) {
                        galleryLauncher.launch("image/*")
                    }
                }, context
            )
            showGalleryOrCameraDialog = false
        })
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen24Dp))
            RoundImage(user = state.getProfile,
                onImageClicked = {
                    showGalleryOrCameraDialog = true
                })


            if (state.getProfile != null) {
                val user = state.getProfile
                TextBold16(title = user.name)
                TextNormal16(title = user.phoneNumber ?: "")
            }

            Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen12Dp))
            ProfileSection(title = stringResource(id = R.string.section_general)) {
                AppLanguage(restartApp)
            }
            ProfileSection(title = stringResource(id = R.string.section_my_content)) {
                ProfileSettingsAndPosts(toMyProducts)
            }
            ProfileSection(title = stringResource(id = R.string.section_account)) {
                ProfileUpdatePasswordAndProfile(
                    updateProfile = toUpdateProfile,
                    updatePassword = toUpdatePassword,
                    toNotifications = toNotifications,
                    toNotificationSettings = toNotificationSettings
                )
            }
            ProfileSection(title = stringResource(id = R.string.section_support)) {
                AboutAppAndContactWithUs()
            }
            ProfileSection(title = stringResource(id = R.string.section_log_out)) {
                LogOut(
                    logOut = {
                        SharedPref.clear()
                        goToRegistration.invoke()
                    },
                    forgotPassword = { toForgotPassword(false) }
                )
            }
            Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen24Dp))

        }
        FreeLoading(state.isLoading)
    }
}


@Composable
fun RoundImage(
    modifier: Modifier = Modifier,
    user: User?,
    onImageClicked: () -> Unit
) {
    var isLoading by remember {
        mutableStateOf(true)
    }
    var isError by remember {
        mutableStateOf(false)
    }
    val url = "${BuildConfig.BASE_URL}user/image/${user?.profileUrl}"
    val imageLoader = rememberAsyncImagePainter(model = url,
        onState = { state ->
            isLoading = state is AsyncImagePainter.State.Loading
            isError = state is AsyncImagePainter.State.Error
        })
    Box(
        modifier = modifier
            .width(100.dp)
            .height(100.dp)
            .clickable {
                onImageClicked.invoke()
            },
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
        modifier = Modifier.fillMaxWidth()
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
    updatePassword: () -> Unit,
    toNotifications: () -> Unit,
    toNotificationSettings: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
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
        Divider(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clip(CircleShape),
            thickness = 0.5.dp
        )
        ProfileColumnItem(
            imageVector = Icons.Filled.Notifications,
            title = stringResource(id = R.string.notifications),
            onItemClicked = toNotifications
        )
        Divider(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clip(CircleShape),
            thickness = 0.5.dp
        )
        ProfileColumnItem(
            imageVector = Icons.Filled.NotificationsActive,
            title = stringResource(id = R.string.notif_settings),
            onItemClicked = toNotificationSettings
        )
    }
}

@Composable
fun ProfileSettingsAndPosts(
    toMyProducts: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogOut(
    logOut: () -> Unit,
    forgotPassword: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
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
            onItemClicked = {
                showBottomSheet = true
            }
        )

    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            Text(
                text = stringResource(id = R.string.log_out),
                fontWeight = FontWeight.Normal,
                fontFamily = robotoFontFamily,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))

            Button(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                            logOut.invoke()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = MaterialTheme.spacing.dimen16Dp)
            ) {
                Text(
                    text = stringResource(id = R.string.logout),
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen24Dp))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLanguage(
    restartApp: () -> Unit
) {

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    ProfileColumnItem(
        imageVector = ImageVector.vectorResource(id = appLanguageNameRes(SharedPref.language)),
        title = stringResource(id = R.string.app_language),
        desc = appLanguageName(SharedPref.language),
        onItemClicked = {
            showBottomSheet = true
        },
        language = true
    )

    val context = LocalContext.current
    val languageSelectListener = remember {
        mutableStateOf(SharedPref.language)
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            RadioGroupExample(
                onLanguageSelected = {
                    languageSelectListener.value = it
                }
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
            Button(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                            if (SharedPref.language != languageSelectListener.value) {
                                SharedPref.language = languageSelectListener.value
                                LocaleManager.setLocale(context, languageSelectListener.value)
                                restartApp.invoke()
                            }
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
    val selectedOption = remember { mutableStateOf(appLanguageName(SharedPref.language)) }

    Column {
        options.forEach { option ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (option == selectedOption.value),
                        onClick = {
                            selectedOption.value = option
                            onLanguageSelected(reverseAppLanguageName(selectedOption.value))
                        }

                    )
                    .padding(8.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (option == selectedOption.value),
                    onClick = {}
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
    onItemClicked: () -> Unit,
    language: Boolean = false
) {
    Row(
        modifier = Modifier
            .clickable { onItemClicked() }
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = MaterialTheme.spacing.dimen16Dp),
        verticalAlignment = Alignment.CenterVertically) {
        if (language) {
            Image(
                modifier = Modifier
                    .width(20.dp)
                    .height(20.dp)
                    .clip(CircleShape),
                imageVector = imageVector, contentDescription = null,
            )
        } else {
            Image(
                modifier = Modifier
                    .width(20.dp)
                    .height(20.dp)
                    .clip(CircleShape),
                imageVector = imageVector, contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen12Dp))
        TextThin(title = title)
        Spacer(modifier = Modifier.weight(1f))
        TextNormal16(title = desc)
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen12Dp))
        Image(
            imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)

        )
    }
}

@Composable
fun ProfileSection(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.dimen16Dp)
    ) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    start = 4.dp,
                    bottom = MaterialTheme.spacing.dimen8Dp
                )
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(color = MaterialTheme.colorScheme.surface),
            content = content
        )
    }
}
