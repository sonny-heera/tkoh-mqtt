package com.tkoh.iot.client.data

import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MqttClientRepository @Inject constructor(
    private val client: Mqtt5AsyncClient,
    private val brokerDao: BrokerDao
) {
    fun getBrokers(): Flow<List<Broker>> {
        return brokerDao.all()
    }

    fun getBroker(name: String): Flow<Broker> {
        return brokerDao.load(name)
    }

    fun deleteBroker(name: String) {
        brokerDao.remove(name)
    }
}