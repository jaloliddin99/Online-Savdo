package org.don.onlineTrade.ui.region

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.don.onlineTrade.ui.home.RegionsScreenState
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

@HiltViewModel
class RegionsViewModel @Inject constructor(): ViewModel() {


    private val _state = mutableStateOf(RegionsScreenState())
    val state: State<RegionsScreenState> = _state

    init {
        getAllRegions(
            token = SharedPref.deviceToken,
            language = SharedPref.language
        )
    }

    private fun getAllRegions(
        token: String,
        language: String,
    ) {

    }



}

data class MyLocation(
    val lat: Double = 0.0,
    val lon: Double = 0.0
)