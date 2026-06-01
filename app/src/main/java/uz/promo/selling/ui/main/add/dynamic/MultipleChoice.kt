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

package uz.promo.selling.ui.main.add.dynamic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.leak.Parameter
import uz.promo.selling.data.remote.models.leak.Values
import uz.promo.selling.ui.theme.spacing
import uz.promo.selling.utils.SharedPref

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultipleChoiceDialog(
    parameter: Parameter,
    onDismiss: (List<Values>) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedItemList by remember {
        mutableStateOf(listOf<Values>())
    }


    ContentWrapper(parameter = parameter) {
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
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen6Dp))
                    FilterChip(
                        onClick = {  },
                        label = {
                            Text(if (SharedPref.language == "ru") it.label_uz else it.label_ru)
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
                onDismiss(selectedItemList)
            },
            sheetState = sheetState,
            modifier = modifier.fillMaxHeight()
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
                },
                onButtonClicked = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                            onDismiss(selectedItemList)
                        }
                    }
                }
            )
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
    onButtonClicked: () -> Unit
) {
    QuestionWrapper(
        modifier = modifier,
        parameter = parameter
    ) {
        possibleAnswers.forEach {
            val selected = selectedAnswers.contains(it)
            CheckboxRow(
                modifier = Modifier.padding(vertical = 8.dp),
                text = if (SharedPref.language == "ru") it.label_uz else it.label_ru,
                selected = selected,
                onOptionSelected = { onOptionSelected(!selected, it) }
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
        Button(
            onClick = onButtonClicked,
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = stringResource(id = R.string.select),
                style = MaterialTheme.typography.titleSmall
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen24Dp))

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

