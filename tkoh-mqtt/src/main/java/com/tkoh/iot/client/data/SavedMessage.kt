package com.tkoh.iot.client.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SavedMessage(
    @PrimaryKey val name: String,
    val topic: String,
    val message: String
)