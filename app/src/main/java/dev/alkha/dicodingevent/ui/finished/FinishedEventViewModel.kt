package dev.alkha.dicodingevent.ui.finished

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alkha.dicodingevent.data.remote.response.EventItem
import dev.alkha.dicodingevent.data.remote.retrofit.ApiConfig
import dev.alkha.dicodingevent.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FinishedEventViewModel() : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<List<EventItem>>> =
        MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState<List<EventItem>>> get() = _uiState

    init {
        getFinishedEvents()
    }

    fun getFinishedEvents() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = ApiConfig.getApiService().getEvents(0)
                _uiState.value = UiState.Success(response.listEvents)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun searchEvents(query: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = ApiConfig.getApiService().searchEvents(query)
                _uiState.value = UiState.Success(response.listEvents)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message.toString())
            }
        }
    }
}