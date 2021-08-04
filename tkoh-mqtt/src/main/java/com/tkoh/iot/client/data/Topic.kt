package com.tkoh.iot.client.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Topic(
    @PrimaryKey val name: String,
    val lastMessage: String
)