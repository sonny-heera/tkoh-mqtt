package com.tkoh.iot.client.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_message")
data class SavedMessage(
    @PrimaryKey val name: String,
    val topic: String,
    val message: String
)