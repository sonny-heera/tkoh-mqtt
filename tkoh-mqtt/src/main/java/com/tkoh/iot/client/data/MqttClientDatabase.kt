package com.tkoh.iot.client.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Broker::class, Topic::class, SavedMessage::class], version = 1)
abstract class MqttClientDatabase : RoomDatabase() {
    abstract fun brokerDao(): BrokerDao
    abstract fun topicDao(): TopicDao
}