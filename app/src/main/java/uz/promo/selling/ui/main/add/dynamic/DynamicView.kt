package uz.promo.selling.ui.main.add.dynamic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import uz.promo.selling.data.remote.models.leak.Parameter
import uz.promo.selling.data.remote.models.post.PostUnitDTO
import uz.promo.selling.data.remote.models.post.PostValueDTO


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DynamicView(
    list: List<Parameter>,
    localParams: Map<String, DynamicViewData>,
    paramListener: (Map<String, DynamicViewData>) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    list.forEach { param ->
        // AI-prefilled value for this param (only when marked valid by the AI merge).
        val prefilled = localParams[param.code]?.takeIf { it.isValid }
        val prefilledKey = prefilled?.post_value?.firstOrNull()?.key
        val prefilledText = prefilled?.post_value?.firstOrNull()?.label_uz
        when (param.type) {
            DynamicView.TYPE_ENUM.type -> {
                if (param.values.size > 2) {
                    DropDownSample(
                        param,
                        initialKey = prefilledKey,
                        onSelectionChanged = { value ->
                            sendData(
                                localParams,
                                param,
                                value.label_uz,
                                value.label_ru,
                                isValid = true
                            ) {
                                paramListener.invoke(it)
                            }
                        }
                    )
                } else if (param.values.size == 2) {
                    HorizontalRadioGroup(
                        selectedItem = { value ->
                            sendData(
                                localParams,
                                param,
                                value.label_uz,
                                value.label_ru,
                                isValid = true
                            ) {
                                paramListener.invoke(it)
                            }
                        },
                        param = param,
                        initialKey = prefilledKey
                    )
                }
            }

            DynamicView.TYPE_NUMBER.type, DynamicView.TYPE_DIGIT.type -> {
                val textFieldState by remember {
                    mutableStateOf(DynamicViewState(txt = prefilledText, regex = param.validation))
                }
                localParams.forEach { (s, dynamicViewData) ->
                    if (param.code == s) {
                        if (dynamicViewData.post_value[0].label_uz != textFieldState.text) {
                            sendData(
                                localParams,
                                param,
                                textFieldState.text,
                                textFieldState.text,
                                isValid = textFieldState.isValid
                            ) {
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
                            PostValueDTO(
                                label_uz = values.label_uz,
                                label_ru = values.label_ru,
                                key = values.key
                            )
                        }
                        sendData(localParams, param, values = valueList, isValid = true) {
                            paramListener.invoke(it)
                        }
                    }
                )
            }

            DynamicView.TYPE_PRICE.type,
            DynamicView.TYPE_SALARY.type -> {
                val textFieldState by remember {
                    mutableStateOf(DynamicViewState(regex = param.validation))
                }
                var units by remember {
                    mutableStateOf(
                        uz.promo.selling.data.remote.models.leak.Unit(
                            code = param.units[0].code,
                            label = param.units[0].label
                        )
                    )
                }
                localParams.forEach { (s, dynamicViewData) ->
                    if (param.code == s) {
                        if (dynamicViewData.post_value[0].label_uz != textFieldState.text) {
                            sendData(
                                localParams,
                                param,
                                label_uz = textFieldState.text,
                                label_ru = textFieldState.text,
                                unit = PostUnitDTO(units.code, units.label),
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
    val label_uz: String,
    val label_ru: String,
    val type: String,
    val post_value: List<PostValueDTO>,
    val unit: PostUnitDTO? = null
)


private inline fun sendData(
    localParams: Map<String, DynamicViewData>,
    param: Parameter,
    label_uz: String? = null,
    label_ru: String? = null,
    values: List<PostValueDTO>? = null,
    unit: PostUnitDTO? = null,
    isValid: Boolean,
    block: (Map<String, DynamicViewData>) -> Unit
) {
    val params = localParams.toMutableMap()
    params[param.code] = DynamicViewData(
        param.validation.is_required,
        isValid = isValid,
        code = param.code,
        label_uz = param.label_uz,
        label_ru = param.label_ru,
        type = param.type,
        post_value = if (label_uz != null && label_ru != null) listOf(
            PostValueDTO(
                label_uz = label_uz,
                label_ru = label_ru,
                key = label_uz
            )
        ) else values!!,
        unit = unit
            ?: if (param.units.isNotEmpty()) PostUnitDTO(
                label = param.units[0].label,
                code = param.units[0].code
            ) else
                null
    )
    block(params)
}

enum class DynamicView(val type: String) {
    TYPE_ENUM("enum"),
    TYPE_DIGIT("digit"),
    TYPE_NUMBER("number"),
    TYPE_SALARY("salary"),
    TYPE_MULTIPLE_CHOICE("multichoice"),
    TYPE_PRICE("price"),
    TYPE_HIDDEN("hidden")
}