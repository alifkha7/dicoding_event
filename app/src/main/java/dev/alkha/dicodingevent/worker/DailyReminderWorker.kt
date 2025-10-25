package dev.alkha.dicodingevent.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.alkha.dicodingevent.data.EventRepository
import dev.alkha.dicodingevent.data.Resource
import dev.alkha.dicodingevent.di.Injection
import dev.alkha.dicodingevent.utils.NotificationHelper
import kotlinx.coroutines.flow.last

class DailyReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val eventRepository: EventRepository = Injection.provideRepository(applicationContext)
    private val notificationHelper = NotificationHelper(applicationContext)

    override suspend fun doWork(): Result {
        return try {
            when (val resource = eventRepository.getEvents(isActive = -1).last()) {
                is Resource.Success -> {
                    resource.data.listEvents.firstOrNull()?.let { event ->
                        notificationHelper.showUpcomingEventReminder(event)
                    }
                }

                is Resource.Error -> {
                    Log.w(WORK_TAG, "Failed to fetch events: ${resource.error}")
                }

                is Resource.Loading -> {}
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(WORK_TAG, "Work failed unexpectedly", e)
            Result.failure()
        }
    }

    companion object {
        const val WORK_TAG = "daily_reminder_worker"
    }
}