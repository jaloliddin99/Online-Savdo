package org.don.onlineTrade.ui.add

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.don.onlineTrade.R
import org.don.onlineTrade.ui.auth.EmailState
import org.don.onlineTrade.ui.auth.EmailStateSaver
import org.don.onlineTrade.ui.theme.spacing


@Composable
fun ChatRoute(
    modifier: Modifier = Modifier
) {
    ChatScreen(modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    modifier: Modifier
) {

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val productTitleState by rememberSaveable(stateSaver = ProductTitleStateSaver) {
        mutableStateOf(ProductTitleState())
    }

    val productDescriptionState by rememberSaveable(stateSaver = ProductDescriptionStateSaver) {
        mutableStateOf(ProductTitleState())
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = MaterialTheme.spacing.dimen16Dp
            ),
        horizontalAlignment = Alignment.Start,
    ) {

        ProductTitle(title = R.string.enter_title)

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen2Dp))

        TextFieldForProduct(
            productState = productTitleState,
            onImeAction = {
                focusRequester.requestFocus()
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))

        ProductTitle(title = R.string.add_description)
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen2Dp))

        TextFieldForProduct(
            productState = productDescriptionState,
            onImeAction = {
                focusRequester.requestFocus()
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))

        ProductTitle(title = R.string.select_category)
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen2Dp))




    }
}



@Composable
fun ProductTitle(@StringRes title: Int) {
    Text(
        text = stringResource(id = title),
        fontWeight = FontWeight.Medium,
        style = MaterialTheme.typography.titleSmall
    )
}