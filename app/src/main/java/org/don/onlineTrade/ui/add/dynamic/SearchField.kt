package org.don.onlineTrade.ui.add.dynamic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.reverse.FeatureMember
import org.don.onlineTrade.ui.add.PostAddressState
import org.don.onlineTrade.ui.add.SearchField
import org.don.onlineTrade.ui.theme.robotoFontFamily


@Composable
fun SearchFieldWithDropdown(
    featureMember: List<FeatureMember>?,
    postAddressState: PostAddressState
){

    var expanded by remember {
        mutableStateOf(false)
    }

    TitleWrapper(titleRes = R.string.enter_address) {
        SearchField(
            productState = postAddressState,
            onImeAction = {

            },
            modifier = Modifier,
            title = R.string.enter_address,
        )
    }

    if (featureMember != null){
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier
        ) {
            featureMember.forEachIndexed { index, entry ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = entry.GeoObject.name,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = robotoFontFamily
                            )
                            Text(
                                text = entry.GeoObject.description,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = robotoFontFamily
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            if (index != featureMember.lastIndex)
                                Divider(modifier = Modifier.height(0.4.dp))
                        }
                    },
                )
            }
        }
    }
}