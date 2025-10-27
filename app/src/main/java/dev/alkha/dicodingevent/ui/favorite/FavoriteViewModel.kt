package dev.alkha.dicodingevent.ui.favorite

import androidx.lifecycle.ViewModel
import dev.alkha.dicodingevent.data.EventRepository

class FavoriteViewModel(private val eventRepository: EventRepository) : ViewModel() {
    fun getFavoriteEvents() = eventRepository.getFavoriteEvents()
}