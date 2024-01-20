package org.don.onlineTrade.ui.map

import android.location.Location
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.don.onlineTrade.domain.repository.LocationTracker
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.LocationReverseUseCase
import org.don.onlineTrade.domain.useCase.regionUseCase.RegionsDistrictsUseCase
import org.don.onlineTrade.ui.home.AddProductScreenState
import org.don.onlineTrade.ui.home.MapScreenScreenState
import org.don.onlineTrade.ui.region.MyLocation
import org.don.onlineTrade.utils.LOCATION_REVERSE_URL
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationReverseUseCase: LocationReverseUseCase,
    private val locationTrackerRepository: LocationTracker,
    private val regionsDistrictsUseCase: RegionsDistrictsUseCase
) :
    ViewModel() {

    private val _state = mutableStateOf(MapScreenScreenState())
    val state: State<MapScreenScreenState> = _state

    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    val query: StateFlow<String> get() = _query

    fun listenRevereTyping(s: String) {
        _query.value = s
    }


    init {
        getRegionDistricts()
        viewModelScope.launch {
            _query
                .filter {
                    it.length > 3
                }.debounce(1000)
                .collectLatest {
                    getLocationReverse(it)
                }
        }
    }

    private fun getLocationReverse(
        addressName: String? = null,
        location: LatLng? = null,
    ) {
        val url =
            if (addressName != null)
                "$LOCATION_REVERSE_URL${addressName}&lang=${SharedPref.language}&format=json"
            else
                "${LOCATION_REVERSE_URL}${location!!.longitude},${location.latitude}&lang=${SharedPref.language}&format=json"

        locationReverseUseCase
            .invoke(url)
            .onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = if (addressName != null)
                            _state.value.copy(
                                isLoading = false,
                                error = "",
                                featureMember = result.data
                            )
                        else _state.value.copy(
                            isLoading = false, error = "",
                            singleFutureMember = result.data,
                        )
                    }

                    is Resource.Error -> {
                        _state.value =
                            _state.value.copy(
                                isLoading = false,
                                error = result.message ?: "An unexpected error occurred"
                            )
                    }

                    is Resource.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = true,
                            error = ""
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }


    private fun getRegionDistricts(
        lang: String = SharedPref.language,
    ) {

        regionsDistrictsUseCase
            .invoke(
                SharedPref.deviceToken,
                lang
            )
            .onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "",
                            regionDistrictData = result.data
                        )
                    }
                    is Resource.Error -> {
                        _state.value =
                            _state.value.copy(
                                isLoading = false,
                                error = result.message ?: "An unexpected error occurred"
                            )
                    }

                    is Resource.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = true,
                            error = ""
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }


    fun locationObserve() = viewModelScope.launch {
        locationTrackerRepository.getCurrentLocation().collectLatest {
            stopLocationUpdates()
            getLocationReverse(location = LatLng(it.latitude, it.longitude))
            _state.value = _state.value.copy(
                latLng = LatLng(it.latitude, it.longitude),
                isLoading = false
            )
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