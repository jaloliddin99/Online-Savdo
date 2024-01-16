package org.don.onlineTrade.ui.add.dynamic

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import org.don.onlineTrade.data.remote.models.leak.Parameter


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DynamicView(
    list: List<Parameter>,
    localParams: Map<String, DynamicViewData>,
    paramListener: (Map<String, DynamicViewData>) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    list.forEach { param ->
        when (param.type) {
            DynamicView.TYPE_ENUM.type -> {
                if (param.values.size > 2) {
                    DropDownSample(
                        param,
                        onSelectionChanged = {
                            val params = localParams.toMutableMap()
                            params[param.code] = DynamicViewData(
                                param.id,
                                param.validation.is_required,
                                isValid = true,
                                value = it.label
                            )
                            paramListener.invoke(params)
                        }
                    )
                } else if (param.values.size == 2) {
                    HorizontalRadioGroup(
                        selectedItem = {

                        },
                        param = param
                    )
                }
            }

            DynamicView.TYPE_NUMBER.type, DynamicView.TYPE_DIGIT.type -> {
                val textFieldState by remember {
                    mutableStateOf(DynamicViewState(regex = param.validation))
                }
                Log.d("TAG", "DynamicViewdawdnawkjdawkjd ${param.code} ${textFieldState.text}")
                DynamicTextView(
                    textState = textFieldState,
                    modifier = Modifier,
                    onImeAction = {
                        keyboardController?.hide()
                    },
                    parameter = param,
                )
            }

            DynamicView.TYPE_MULTIPLE_CHOICE.type -> {

                MultipleChoiceDialog(
                    parameter = param,
                    onDismiss = {

                    }
                )
            }

            DynamicView.TYPE_PRICE.type -> {
                val textFieldState by remember {
                    mutableStateOf(DynamicViewState(regex = param.validation))
                }
                var units by remember {
                    mutableStateOf(
                        org.don.onlineTrade.data.remote.models.leak.Unit(
                            code = param.units[0].code,
                            label = param.units[0].label
                        )
                    )
                }
                PriceSelector(
                    textState = textFieldState,
                    modifier = Modifier,
                    onImeAction = {
                        keyboardController?.hide()
                    },
                    parameter = param,
                    unit = units,
                    onUnitSelected = { selectedUnit ->
                        units = selectedUnit
                    }
                )
            }
        }
    }
}

data class DynamicViewData(
    val paramId: Int,
    val isRequired: Boolean,
    val isValid: Boolean = false,
    val value: String = ""
)

enum class DynamicView(val type: String) {
    TYPE_ENUM("enum"),
    TYPE_DIGIT("digit"),
    TYPE_NUMBER("number"),
    TYPE_MULTIPLE_CHOICE("multichoice"),
    TYPE_PRICE("price"),
    TYPE_HIDDEN("hidden")
}