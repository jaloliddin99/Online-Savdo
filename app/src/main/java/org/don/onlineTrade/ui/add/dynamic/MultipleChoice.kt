/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.don.onlineTrade.ui.add.dynamic

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.leak.Parameter
import org.don.onlineTrade.data.remote.models.leak.Values
import org.don.onlineTrade.ui.add.ImagesDisplayView
import org.don.onlineTrade.ui.add.ShowCircularImage
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultipleChoiceDialog(
    parameter: Parameter,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedItemList by remember {
        mutableStateOf(listOf<Values>())
    }

    Column {
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
        Text(
            text = "${parameter.label}${if (parameter.validation.is_required) "*" else ""}",
            fontSize = MaterialTheme.spacing.dimen16Sp,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen4Dp))
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            onClick = {
                showBottomSheet = true
            }
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(MaterialTheme.spacing.dimen12Dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(selectedItemList) {
                    FilterChip(
                        onClick = {  },
                        label = {
                            Text(it.label)
                        },
                        selected = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        },
                    )

                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            MultipleChoiceQuestion(
                parameter = parameter,
                possibleAnswers = parameter.values,
                selectedAnswers = selectedItemList,
                onOptionSelected = { selected: Boolean, answer: Values ->
                    if (selected){
                        selectedItemList = selectedItemList + answer
                    }else {
                        val list = selectedItemList.toMutableList()
                        list.remove(answer)
                        selectedItemList = list
                    }
                }
            )
            Button(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = MaterialTheme.spacing.dimen16Dp)
            ) {
                Text(
                    text = stringResource(id = R.string.select),
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen24Dp))
        }
    }
}



@Composable
fun MultipleChoiceQuestion(
    parameter: Parameter,
    possibleAnswers: List<Values>,
    selectedAnswers: List<Values>,
    onOptionSelected: (selected: Boolean, answer: Values) -> Unit,
    modifier: Modifier = Modifier,
) {
    QuestionWrapper(
        modifier = modifier,
        parameter = parameter
    ) {
        possibleAnswers.forEach {
            val selected = selectedAnswers.contains(it)
            CheckboxRow(
                modifier = Modifier.padding(vertical = 8.dp),
                text = it.label,
                selected = selected,
                onOptionSelected = { onOptionSelected(!selected, it) }
            )
        }
    }
}

@Composable
fun CheckboxRow(
    text: String,
    selected: Boolean,
    onOptionSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        ),
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onOptionSelected)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Box(Modifier.padding(8.dp)) {
                Checkbox(selected, onCheckedChange = null)
            }
        }
    }
}

//@Preview
//@Composable
//fun MultipleChoiceQuestionPreview() {
//    val possibleAnswers = listOf(R.string.success, R.string.continuee, R.string.camera)
//    val selectedAnswers = remember { mutableStateListOf(R.string.Dark) }
//    MultipleChoiceQuestion(
//        titleResourceId = R.string.logout,
//        directionsResourceId = R.string.forgot_password,
//        possibleAnswers = possibleAnswers,
//        selectedAnswers = selectedAnswers,
//        onOptionSelected = { _, _ -> }
//    )
//}
