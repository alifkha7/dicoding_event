package dev.alkha.dicodingevent.data

import android.content.Context
import dev.alkha.dicodingevent.data.local.room.EventDatabase
import dev.alkha.dicodingevent.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        return EventRepository.getInstance(apiService, dao)
    }
}