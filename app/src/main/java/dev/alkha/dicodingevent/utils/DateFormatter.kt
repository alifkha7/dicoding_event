package dev.alkha.dicodingevent.utils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    private const val SIMPLE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private const val TARGET_FORMAT = "dd MMM yyyy"

    fun formatDate(currentDate: String): String? {
        val date = parseDate(currentDate)
        if (date != null) {
            val targetDf: DateFormat = SimpleDateFormat(TARGET_FORMAT, Locale.getDefault())
            return targetDf.format(date)
        }
        return currentDate
    }

    private fun parseDate(currentDate: String): Date? {
        try {
            val currentDf: DateFormat = SimpleDateFormat(SIMPLE_FORMAT, Locale.getDefault())
            return currentDf.parse(currentDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }
}