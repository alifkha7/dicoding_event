package dev.alkha.dicodingevent.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alkha.dicodingevent.data.remote.response.EventItem
import dev.alkha.dicodingevent.data.remote.retrofit.ApiConfig
import dev.alkha.dicodingevent.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _upcomingEventsState = MutableStateFlow<UiState<List<EventItem>>>(UiState.Loading)
    val upcomingEventsState: StateFlow<UiState<List<EventItem>>> =
        _upcomingEventsState.asStateFlow()

    private val _finishedEventsState = MutableStateFlow<UiState<List<EventItem>>>(UiState.Loading)
    val finishedEventsState: StateFlow<UiState<List<EventItem>>> =
        _finishedEventsState.asStateFlow()

    init {
        getUpcomingEvents()
        getFinishedEvents()
    }

    private fun getUpcomingEvents() {
        viewModelScope.launch {
            _upcomingEventsState.value = UiState.Loading
            try {
                val response = ApiConfig.getApiService().getEvents(1)
                _upcomingEventsState.value = UiState.Success(response.listEvents.take(5))
            } catch (e: Exception) {
                _upcomingEventsState.value = UiState.Error(e.message.toString())
            }
        }
    }

    private fun getFinishedEvents() {
        viewModelScope.launch {
            _finishedEventsState.value = UiState.Loading
            try {
                val response = ApiConfig.getApiService().getEvents(0)
                _finishedEventsState.value = UiState.Success(response.listEvents.take(5))
            } catch (e: Exception) {
                _finishedEventsState.value = UiState.Error(e.message.toString())
            }
        }
    }
}