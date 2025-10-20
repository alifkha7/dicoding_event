package dev.alkha.dicodingevent.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.alkha.dicodingevent.R
import dev.alkha.dicodingevent.data.EventRepository
import dev.alkha.dicodingevent.data.Injection
import dev.alkha.dicodingevent.data.Resource
import kotlinx.coroutines.flow.first

class DailyReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val eventRepository: EventRepository = Injection.provideRepository(applicationContext)

    override suspend fun doWork(): Result {
        return try {
            when (val response = eventRepository.getEvents(isActive = -1).first()) {
                is Resource.Success -> {
                    val event = response.data.listEvents.firstOrNull()

                    if (event != null) {
                        showNotification(
                            event.name,
                            "Recommendation event for you on ${event.beginTime}"
                        )
                    }
                }

                else -> {}
            }

            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }

    private fun showNotification(title: String, description: String) {
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(NOTIFICATION_ID, notification.build())
        }
    }

    companion object {
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "dicoding event channel"
        private const val NOTIFICATION_ID = 1
    }
}