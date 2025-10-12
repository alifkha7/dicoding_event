package dev.alkha.dicodingevent.ui.upcoming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alkha.dicodingevent.data.remote.response.EventItem
import dev.alkha.dicodingevent.data.remote.retrofit.ApiConfig
import dev.alkha.dicodingevent.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UpcomingEventViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<EventItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<EventItem>>> = _uiState

    init {
        getUpcomingEvents()
    }

    private fun getUpcomingEvents() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = ApiConfig.getApiService().getEvents(1)
                _uiState.value = UiState.Success(response.listEvents)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message.toString())
            }
        }
    }
}