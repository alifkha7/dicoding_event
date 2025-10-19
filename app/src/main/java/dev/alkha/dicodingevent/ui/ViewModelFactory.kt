package dev.alkha.dicodingevent.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.alkha.dicodingevent.data.EventRepository
import dev.alkha.dicodingevent.data.Injection
import dev.alkha.dicodingevent.ui.detail.DetailEventViewModel
import dev.alkha.dicodingevent.ui.event.EventViewModel
import dev.alkha.dicodingevent.ui.favorite.FavoriteViewModel
import dev.alkha.dicodingevent.ui.setting.SettingPreferences
import dev.alkha.dicodingevent.ui.setting.SettingViewModel
import dev.alkha.dicodingevent.ui.setting.dataStore

class ViewModelFactory private constructor(
    private val eventRepository: EventRepository,
    private val settingPreferences: SettingPreferences,
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            return EventViewModel(eventRepository) as T
        } else if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(eventRepository) as T
        } else if (modelClass.isAssignableFrom(DetailEventViewModel::class.java)) {
            return DetailEventViewModel(eventRepository) as T
        } else if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return SettingViewModel(settingPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideRepository(context),
                    SettingPreferences.getInstance(context.dataStore)
                )
            }.also { instance = it }
    }
}