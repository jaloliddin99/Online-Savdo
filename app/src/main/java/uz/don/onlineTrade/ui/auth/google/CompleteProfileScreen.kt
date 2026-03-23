package uz.don.onlineTrade.ui.auth.google

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.don.onlineTrade.R
import uz.don.onlineTrade.ui.auth.PhoneNumber
import uz.don.onlineTrade.ui.auth.PhoneNumberState
import uz.don.onlineTrade.ui.auth.register.Branding
import uz.don.onlineTrade.ui.auth.removeFirstCharacterAndAllSpaces
import uz.don.onlineTrade.ui.theme.robotoFontFamily
import uz.don.onlineTrade.utils.FreeLoading

private val BrandGreen = Color(0xFF1A6B3C)

@Composable
fun CompleteProfileScreen(
    isLoading: Boolean,
    onSubmit: (phoneNumber: String) -> Unit
) {
    val phoneState = remember { PhoneNumberState() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Branding(text = R.string.complete_profile_subtitle)

        Spacer(modifier = Modifier.height(48.dp))

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

        PhoneNumber(
            phoneState = phoneState,
            onImeAction = {},
            modifier = Modifier
        )

        Button(
            onClick = {
                if (phoneState.isValid) {
                    onSubmit(removeFirstCharacterAndAllSpaces(phoneState.text))
                }
            },
            enabled = phoneState.isValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
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
