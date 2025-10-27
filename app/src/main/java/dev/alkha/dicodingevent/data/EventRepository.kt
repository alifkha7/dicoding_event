package dev.alkha.dicodingevent.data

import androidx.lifecycle.LiveData
import dev.alkha.dicodingevent.data.local.entity.EventEntity
import dev.alkha.dicodingevent.data.local.room.EventDao
import dev.alkha.dicodingevent.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class EventRepository(private val apiService: ApiService, private val eventDao: EventDao) {

    fun getEvents(isActive: Int) = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getEvents(isActive)
            emit(Resource.Success(response))
        } catch (_: Exception) {
            emit(Resource.Error(ERROR_MESSAGE))
        }
    }.flowOn(Dispatchers.IO)

    fun getEventDetail(id: Int) = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getEventDetail(id)
            emit(Resource.Success(response))
        } catch (_: Exception) {
            emit(Resource.Error(ERROR_MESSAGE))
        }
    }.flowOn(Dispatchers.IO)

    fun searchEvents(query: String) = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.searchEvents(query)
            emit(Resource.Success(response))
        } catch (_: Exception) {
            emit(Resource.Error(ERROR_MESSAGE))
        }
    }.flowOn(Dispatchers.IO)

    fun getFavoriteEvents(): LiveData<List<EventEntity>> = eventDao.getFavoriteEvents()

    fun getEventById(id: Int): LiveData<EventEntity?> = eventDao.getEventById(id)

    suspend fun setEventFavorite(event: EventEntity) {
        eventDao.insert(event)
    }

    suspend fun removeEventFavorite(event: EventEntity) {
        eventDao.delete(event)
    }

    companion object {
        private const val ERROR_MESSAGE = "Oops, something went wrong. Please try again later."

        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao,
        ): EventRepository = instance ?: synchronized(this) {
            instance ?: EventRepository(apiService, eventDao)
        }.also { instance = it }
    }
}