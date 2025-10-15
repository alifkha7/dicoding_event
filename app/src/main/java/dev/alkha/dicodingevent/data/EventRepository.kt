package dev.alkha.dicodingevent.data

import dev.alkha.dicodingevent.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.flow

class EventRepository(private val apiService: ApiService) {

    fun getEvents(isActive: Int) = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getEvents(isActive)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun getEventDetail(id: Int) = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getEventDetail(id)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun searchEvents(query: String) = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.searchEvents(query)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): EventRepository = instance ?: synchronized(this) {
            instance ?: EventRepository(apiService)
        }.also { instance = it }
    }
}