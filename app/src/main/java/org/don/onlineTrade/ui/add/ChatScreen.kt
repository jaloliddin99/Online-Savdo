package org.don.onlineTrade.ui.add

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.don.onlineTrade.R
import org.don.onlineTrade.ui.theme.spacing


@Composable
fun ChatRoute(
    navigateToCategories: () -> Unit,
    navigateToSelectRegions: () -> Unit,
    modifier: Modifier = Modifier
) {
    ChatScreen(
        modifier = modifier,
        navigateToCategories,
        navigateToSelectRegions
    )
}

@Composable
fun ChatScreen(
    modifier: Modifier,
    navigateToCategories: () -> Unit,
    navigateToSelectRegions: () -> Unit
) {

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val productTitleState by rememberSaveable(stateSaver = ProductTitleStateSaver) {
        mutableStateOf(ProductTitleState())
    }

    val productDescriptionState by rememberSaveable(stateSaver = ProductDescriptionStateSaver) {
        mutableStateOf(ProductTitleState())
    }

    val categoryState by rememberSaveable(stateSaver = CategoryStateSaver) {
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
            modifier = Modifier
                .fillMaxWidth()
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
                .height(200.dp),
            title = R.string.please_enter_description
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
        ProductTitle(title = R.string.select_category)
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen2Dp))

        TextFieldUnEditable(
            productState = categoryState,
            modifier = Modifier.fillMaxWidth(),
            title = R.string.please_select_category,
            isFocusedOrClicked = navigateToCategories

        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
        ProductTitle(title = R.string.select_region)
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen2Dp))

        TextFieldUnEditable(
            productState = categoryState,
            modifier = Modifier.fillMaxWidth(),
            title = R.string.please_select_region,
            isFocusedOrClicked = navigateToSelectRegions
        )


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

