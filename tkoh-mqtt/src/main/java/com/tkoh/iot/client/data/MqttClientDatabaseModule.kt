package com.tkoh.iot.client.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class MqttClientDatabaseModule {

    @Provides
    @Singleton
    fun provideMqttClientDatabase(@ApplicationContext context: Context) : MqttClientDatabase {
        return Room.databaseBuilder(context,
            MqttClientDatabase::class.java,
            MqttClientDatabase::class.simpleName.toString())
            .build()
    }

    @Provides
    fun provideBrokerDao(mqttClientDatabase: MqttClientDatabase) : BrokerDao {
        return mqttClientDatabase.brokerDao()
    }

    @Provides
    fun provideTopicDao(mqttClientDatabase: MqttClientDatabase) : TopicDao {
        return mqttClientDatabase.topicDao()
    }

    @Provides
    fun provideSavedMessageDao(mqttClientDatabase: MqttClientDatabase) : SavedMessageDao {
        return mqttClientDatabase.savedMessageDao()
    }
}