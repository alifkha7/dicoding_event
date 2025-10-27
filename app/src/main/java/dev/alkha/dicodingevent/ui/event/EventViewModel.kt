package dev.alkha.dicodingevent.ui.event

import androidx.lifecycle.ViewModel
import dev.alkha.dicodingevent.data.EventRepository

class EventViewModel(private val eventRepository: EventRepository) : ViewModel() {

    fun getEvents(isActive: Int) = eventRepository.getEvents(isActive)

    fun searchEvents(query: String) = eventRepository.searchEvents(query)

}