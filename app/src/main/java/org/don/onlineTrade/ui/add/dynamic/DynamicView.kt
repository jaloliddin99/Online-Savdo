package org.don.onlineTrade.ui.add.dynamic

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import org.don.onlineTrade.data.remote.models.leak.Parameter
import org.don.onlineTrade.ui.add.ProductPriceStateSaver


@Composable
fun DynamicView(
    list: List<Parameter>
) {


    list.forEach {
        when (it.type) {
            DynamicView.TYPE_ENUM.type -> {
                if (it.values.size > 2) {
                    DropDownSample(
                        it,
                        onSelectionChanged = {

                        }
                    )
                } else if (it.values.size == 2) {
                    HorizontalRadioGroup(
                        selectedItem = {

                        },
                        param = it
                    )
                }
            }

            DynamicView.TYPE_NUMBER.type, DynamicView.TYPE_DIGIT.type -> {
                val textFieldState by remember {
                    mutableStateOf(DynamicViewState(regex = it.validation.pattern))
                }
                DynamicTextView(
                    textState = textFieldState,
                    modifier = Modifier,
                    onImeAction = {

                    },
                    parameter = it,
                )
            }

            DynamicView.TYPE_MULTIPLE_CHOICE.type -> {

            }
        }
    }


}

enum class DynamicView(val type: String) {
    TYPE_ENUM("enum"),
    TYPE_DIGIT("digit"),
    TYPE_NUMBER("number"),
    TYPE_MULTIPLE_CHOICE("multichoice"),
    TYPE_HIDDEN("hidden")
}