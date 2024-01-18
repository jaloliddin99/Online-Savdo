/*
 * Copyright 2023 The Android Open Source Project
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.don.onlineTrade.data.remote.models.leak.Parameter
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.slightlyDeemphasizedAlpha
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.ui.theme.stronglyDeemphasizedAlpha
import org.don.onlineTrade.utils.SharedPref

/**
 * A scrollable container with the question's title, direction, and dynamic content.
 *
 * @param titleResourceId String resource to use for the question's title
 * @param modifier Modifier to apply to the entire wrapper
 * @param directionsResourceId String resource to use for the question's directions; the direction
 * UI will be omitted if null is passed
 * @param content Composable to display after the title and option directions
 */
@Composable
fun QuestionWrapper(
    parameter: Parameter,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
        Text(
            text = "${translator(parameter.label_uz, parameter.label_ru)}${if (parameter.validation.is_required) "*" else ""}",
            fontSize = MaterialTheme.spacing.dimen16Sp,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen4Dp))

        content()
    }
}

@Composable
fun ContentWrapper(
    parameter: Parameter,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
        Text(
            text = "${translator(parameter.label_uz, parameter.label_ru)}${if (parameter.validation.is_required) "*" else ""}",
            fontSize = MaterialTheme.spacing.dimen16Sp,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen4Dp))

        content()
    }
}


@Composable
fun TitleWrapper(
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
        Text(
            text = stringResource(id = titleRes),
            fontSize = MaterialTheme.spacing.dimen16Sp,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen4Dp))
        content()
    }
}


val translator: (uz: String, ru: String?) -> String = { uz, ru ->
    if (SharedPref.language == "ru"){
        ru?:""
    }else
        uz
}