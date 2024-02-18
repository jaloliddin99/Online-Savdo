package org.don.onlineTrade.ui.home.search

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

// Define showDatePicker inside the composable function

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDatePicker(currentDate: LocalDate?, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current // Safely obtain the Context

    val calendar = Calendar.getInstance().apply {
        currentDate?.let {
            set(Calendar.YEAR, it.year)
            set(Calendar.MONTH, it.monthValue - 1)
            set(Calendar.DAY_OF_MONTH, it.dayOfMonth)
        }
    }

    DatePickerDialog(
        content = {

        },

        onDismissRequest = {

        },
        confirmButton = {

        }
    )
}