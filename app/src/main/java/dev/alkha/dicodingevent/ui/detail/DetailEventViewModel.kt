package dev.alkha.dicodingevent.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alkha.dicodingevent.data.EventRepository
import dev.alkha.dicodingevent.data.local.entity.EventEntity
import kotlinx.coroutines.launch

class DetailEventViewModel(private val eventRepository: EventRepository) : ViewModel() {
    fun getDetailEvent(id: Int) = eventRepository.getEventDetail(id)

    fun setFavoriteEvent(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.setEventFavorite(event)
        }
    }

    fun removeFavoriteEvent(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.removeEventFavorite(event)
        }
    }

    fun getEventById(id: Int) = eventRepository.getEventById(id)
}