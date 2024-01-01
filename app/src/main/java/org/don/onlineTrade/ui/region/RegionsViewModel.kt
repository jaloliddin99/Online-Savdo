package org.don.onlineTrade.ui.region

import android.location.Location
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.don.onlineTrade.data.remote.models.region.DataDistrict
import org.don.onlineTrade.domain.repository.LocationTracker
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.regionUseCase.RegionsUseCase
import org.don.onlineTrade.ui.add.ProductTitleState
import org.don.onlineTrade.ui.auth.TextFieldState
import org.don.onlineTrade.ui.home.RegionsScreenState
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

@HiltViewModel
class RegionsViewModel @Inject constructor(
    private val regionsUseCase: RegionsUseCase,
    private val locationTrackerRepository: LocationTracker,
    ): ViewModel() {


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
        regionsUseCase(
            token,
            language,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = RegionsScreenState(regions = result.data)
                }

                is Resource.Error -> {
                    _state.value = RegionsScreenState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = RegionsScreenState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }


    fun updateDistrictAndShowAlertDialog(district: DataDistrict){
        _state.value = _state.value.copy(district = district, showAlertDialog = true)

    }
    fun hideAlertDialog() {
        _state.value = _state.value.copy(showAlertDialog = false)
    }


    fun getAllDistricts(
        token: String = SharedPref.deviceToken,
        language: String = SharedPref.language,
        regionId: Int
    ) {
        regionsUseCase.invokeDistricts(
            token,
            language,
            regionId
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = RegionsScreenState(districts = result.data)
                }

                is Resource.Error -> {
                    _state.value = RegionsScreenState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = RegionsScreenState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun locationObserve() = viewModelScope.launch {
        locationTrackerRepository.getCurrentLocation().collectLatest {
            stopLocationUpdates()
            _state.value = _state.value.copy(myLocation = MyLocation(it.latitude, it.longitude),
                isLoading = false)
        }
    }

    fun startLocationUpdates() {
        _state.value = _state.value.copy(isLoading = true)
        locationTrackerRepository.startLocationUpdate()
    }

    private fun stopLocationUpdates() {
        locationTrackerRepository.stopLocationUpdate()
    }

}

data class MyLocation(
    val lat: Double = 0.0,
    val lon: Double = 0.0
)