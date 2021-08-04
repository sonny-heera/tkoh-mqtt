package com.tkoh.iot.client.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Broker(
    @PrimaryKey val name: String,
    val id: String,
    val address: String,
    val port: Int
)