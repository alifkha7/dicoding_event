package dev.alkha.dicodingevent.di

import android.content.Context
import dev.alkha.dicodingevent.data.EventRepository
import dev.alkha.dicodingevent.data.local.room.EventDatabase
import dev.alkha.dicodingevent.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.Companion.getApiService()
        val database = EventDatabase.Companion.getInstance(context)
        val dao = database.eventDao()
        return EventRepository.Companion.getInstance(apiService, dao)
    }
}