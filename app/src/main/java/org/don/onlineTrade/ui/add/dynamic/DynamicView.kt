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
                        onSelectionChanged = { value ->
                            sendData(localParams, param, value.label, isValid = true) {
                                paramListener.invoke(it)
                            }
                        }
                    )
                } else if (param.values.size == 2) {
                    HorizontalRadioGroup(
                        selectedItem = { value ->
                            sendData(localParams, param, value.label, isValid = true) {
                                paramListener.invoke(it)
                            }
                        },
                        param = param
                    )
                }
            }

            DynamicView.TYPE_NUMBER.type, DynamicView.TYPE_DIGIT.type -> {
                val textFieldState by remember {
                    mutableStateOf(DynamicViewState(regex = param.validation))
                }
                localParams.forEach { (s, dynamicViewData) ->
                    if (param.code == s) {
                        if (dynamicViewData.postValues[0].label != textFieldState.text) {
                            Log.d("TAG", "DynamicViewdawdawdddnakwjd ${textFieldState.text}, ${textFieldState.isValid}")
                            sendData(localParams, param, textFieldState.text, isValid = textFieldState.isValid) {
                                paramListener.invoke(it)
                            }
                        }
                    }
                }
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
                    onDismiss = { it ->
                        val valueList = it.map { values ->
                            PostValuesDTO(label = values.label)
                        }
                        sendData(localParams, param, values = valueList, isValid = true) {
                            paramListener.invoke(it)
                        }
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
                localParams.forEach { (s, dynamicViewData) ->
                    if (param.code == s) {
                        if (dynamicViewData.postValues[0].label != textFieldState.text) {
                            Log.d("TAG", "DynamicViewdawdawdddnakwjd modey ${textFieldState.text}, ${textFieldState.isValid}")

                            sendData(
                                localParams,
                                param,
                                label = textFieldState.text,
                                unit = PostUnit(units.label),
                                isValid = textFieldState.isValid
                            ) {
                                paramListener.invoke(it)
                            }
                        }
                    }
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
    val isRequired: Boolean,
    val isValid: Boolean = false,
    val code: String,
    val label: String,
    val type: String,
    val postValues: List<PostValuesDTO>,
    val unit: PostUnit? = null
)

private inline fun sendData(
    localParams: Map<String, DynamicViewData>,
    param: Parameter,
    label: String? = null,
    values: List<PostValuesDTO>? = null,
    unit: PostUnit? = null,
    isValid: Boolean,
    block: (Map<String, DynamicViewData>) -> Unit
) {
    val params = localParams.toMutableMap()
    params[param.code] = DynamicViewData(
        param.validation.is_required,
        isValid = isValid,
        code = param.code,
        label = param.label,
        type = param.type,
        postValues = if (label != null) listOf(
            PostValuesDTO(label = label)
        ) else values!!,
        unit = unit
            ?: if (param.units.isNotEmpty()) PostUnit(
                label = param.units[0].label
            ) else
                null
    )
    block(params)
}

data class PostUnit(
    val label: String = ""
)

data class PostValuesDTO(
    val label: String
)

enum class DynamicView(val type: String) {
    TYPE_ENUM("enum"),
    TYPE_DIGIT("digit"),
    TYPE_NUMBER("number"),
    TYPE_MULTIPLE_CHOICE("multichoice"),
    TYPE_PRICE("price"),
    TYPE_HIDDEN("hidden")
}