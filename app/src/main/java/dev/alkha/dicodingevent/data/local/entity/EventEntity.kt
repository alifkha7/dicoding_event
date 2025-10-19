package dev.alkha.dicodingevent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val summary: String,
    val mediaCover: String,
    var isFavorite: Boolean = false,
)
