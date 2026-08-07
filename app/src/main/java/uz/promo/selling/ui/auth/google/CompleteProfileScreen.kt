package uz.promo.selling.ui.auth.google

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import uz.promo.selling.BuildConfig
import uz.promo.selling.R
import uz.promo.selling.ui.auth.NameField
import uz.promo.selling.ui.auth.PhoneNumber
import uz.promo.selling.ui.auth.PhoneNumberState
import uz.promo.selling.ui.auth.TextFieldState
import uz.promo.selling.ui.auth.removeFirstCharacterAndAllSpaces
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.utils.FreeLoading

private val BrandGreen = Color(0xFF1A6B3C)

/**
 * Final step of a Google/Apple sign-up. Name and lastname are prefilled from
 * whatever the provider gave us (Google supplies both; Apple usually neither)
 * and are required to continue. The photo is optional — a provider avatar is
 * shown if one was imported, and the user may replace it or skip it entirely.
 */
@Composable
fun CompleteProfileScreen(
    isLoading: Boolean,
    prefillName: String = "",
    prefillLastname: String = "",
    prefillPhotoUrl: String? = null,
    onSubmit: (name: String, lastname: String, phoneNumber: String, photoUri: Uri?) -> Unit
) {
    val phoneState = remember { PhoneNumberState() }
    val nameState = remember(prefillName) {
        TextFieldState(validator = { it.trim().length >= 2 }).apply { text = prefillName }
    }
    val lastnameState = remember(prefillLastname) {
        TextFieldState(validator = { it.trim().length >= 2 }).apply { text = prefillLastname }
    }

    var pickedPhoto by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { pickedPhoto = it } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.complete_profile_subtitle),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(R.string.complete_profile_title),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = stringResource(R.string.complete_profile_description),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        // Avatar picker. Shows the newly picked image if there is one, otherwise
        // the avatar imported from the provider, otherwise a camera placeholder.
        val existingPhotoUrl = prefillPhotoUrl?.takeIf { it.isNotBlank() }
            ?.let { "${BuildConfig.BASE_URL}user/image/$it" }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                val model = pickedPhoto ?: existingPhotoUrl
                if (model != null) {
                    AsyncImage(
                        model = model,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = stringResource(
                    if (pickedPhoto != null || existingPhotoUrl != null)
                        R.string.complete_profile_change_photo
                    else R.string.complete_profile_add_photo
                ),
                fontFamily = robotoFontFamily,
                fontSize = 14.sp,
                color = BrandGreen,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 20.dp)
                    .clickable { galleryLauncher.launch("image/*") }
            )
        }

        NameField(
            nameState = nameState,
            imeAction = ImeAction.Next,
            modifier = Modifier,
            resId = R.string.firstName
        )

        Spacer(modifier = Modifier.height(8.dp))

        NameField(
            nameState = lastnameState,
            imeAction = ImeAction.Next,
            modifier = Modifier,
            resId = R.string.lastName
        )

        Spacer(modifier = Modifier.height(8.dp))

        PhoneNumber(
            phoneState = phoneState,
            onImeAction = {},
            modifier = Modifier
        )

        val isEnabled = phoneState.isValid && nameState.isValid && lastnameState.isValid

        Button(
            onClick = {
                if (isEnabled) {
                    onSubmit(
                        nameState.text.trim(),
                        lastnameState.text.trim(),
                        removeFirstCharacterAndAllSpaces(phoneState.text),
                        pickedPhoto
                    )
                }
            },
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 32.dp)
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandGreen,
                contentColor = Color.White,
                disabledContainerColor = BrandGreen.copy(alpha = 0.4f),
                disabledContentColor = Color.White.copy(alpha = 0.7f)
            )
        ) {
            Text(
                text = stringResource(R.string.continuee),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
    FreeLoading(isLoading, paddingTop = 64.dp)
}
