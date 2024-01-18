package org.don.onlineTrade.ui.home.search

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
import org.don.onlineTrade.ui.home.AddProductScreenState
import org.don.onlineTrade.ui.home.MapScreenScreenState
import org.don.onlineTrade.ui.region.MyLocation
import org.don.onlineTrade.utils.LOCATION_REVERSE_URL
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MapViewModel @Inject constructor(private val locationReverseUseCase: LocationReverseUseCase,
                                       private val locationTrackerRepository: LocationTracker,) :
    ViewModel() {

    private val _state = mutableStateOf(MapScreenScreenState())
    val state: State<MapScreenScreenState> = _state

    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    val query: StateFlow<String> get() = _query

    fun listenRevereTyping(s: String) {
        _query.value = s
    }


    init {
        viewModelScope.launch {
            _query
                .filter {
                    it.length > 3
                }.debounce(1000)
                .collectLatest {
                    Log.d("TAG", "AddProductScreedawdawkdjawkdjn2 ${it},")

                    getLocationReverse(it)
                }
        }
    }

    private fun getLocationReverse(
        addressName: String
    ) {
        val url = "$LOCATION_REVERSE_URL${addressName}&lang=${SharedPref.language}&format=json"
        locationReverseUseCase
            .invoke(url)
            .onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = MapScreenScreenState(featureMember = result.data)
                    }

                    is Resource.Error -> {
                        _state.value =
                            MapScreenScreenState(
                                error = result.message ?: "An unexpected error occurred"
                            )
                    }

                    is Resource.Loading -> {
                        _state.value = MapScreenScreenState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
    }



    fun hideAlertDialog() {
        _state.value = _state.value.copy(showAlertDialog = false)
    }


    fun locationObserve() = viewModelScope.launch {
        locationTrackerRepository.getCurrentLocation().collectLatest {
            stopLocationUpdates()
            _state.value = _state.value.copy(latLng = LatLng(it.latitude, it.longitude),
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