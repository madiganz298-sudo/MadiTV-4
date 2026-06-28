package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channels")
data class Channel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val logoUrl: String?,
    val groupName: String,
    val streamUrl: String,
    val playlistId: Int,
    var isFavorite: Boolean = false,
    var lastWatched: Long = 0,
    var isOnline: Boolean = true
)
