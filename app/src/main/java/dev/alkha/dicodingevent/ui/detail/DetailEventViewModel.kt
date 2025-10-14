package dev.alkha.dicodingevent.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alkha.dicodingevent.data.remote.response.EventItem
import dev.alkha.dicodingevent.data.remote.retrofit.ApiConfig
import dev.alkha.dicodingevent.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailEventViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<EventItem>>(UiState.Loading)
    val uiState: StateFlow<UiState<EventItem>> = _uiState

    fun getDetailEvent(id: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = ApiConfig.getApiService().getEventDetail(id)
                _uiState.value = UiState.Success(response.event)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message.toString())
            }
        }
    }
}