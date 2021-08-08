package com.tkoh.iot.client.data

import android.annotation.SuppressLint
import android.content.Context
import com.hivemq.client.mqtt.MqttClientState
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import java.util.*

const val ADDRESS_NOT_SET = "Broker address not set"
const val PORT_NOT_SET = 0

@Module
@InstallIn(SingletonComponent::class)
object Mqtt5AsyncClientModule {

    @Provides
    fun provideMqtt5AsyncClient(
        @ApplicationContext context: Context
    ) : Mqtt5AsyncClient {
        val brokerAddress = EncryptedSharedPreferencesUtils.getBrokerAddress(context)
        val brokerPort = EncryptedSharedPreferencesUtils.getBrokerPort(context)

        return createClient(brokerAddress, brokerPort, context)
    }

    private fun createClient(brokerAddress : String?, brokerPort : Int,
            context: Context) : Mqtt5AsyncClient {
        return if (brokerAddress != null && brokerPort != PORT_NOT_SET) {
            buildAndConnectClient(brokerAddress, brokerPort, context)
        } else {
            EncryptedSharedPreferencesUtils.setBrokerState(MqttClientState.DISCONNECTED.toString(), context)

            Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(ADDRESS_NOT_SET)
                .serverPort(PORT_NOT_SET)
                .buildAsync()
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    private fun buildAndConnectClient(brokerAddress: String, brokerPort: Int, context: Context) :
            Mqtt5AsyncClient {
        val client = Mqtt5Client.builder()
            .identifier(UUID.randomUUID().toString())
            .serverHost(brokerAddress)
            .serverPort(brokerPort)
            .buildAsync()

        EncryptedSharedPreferencesUtils.setBrokerState(MqttClientState.CONNECTING.toString(), context)

        client.connect()
            .whenComplete { ack, throwable ->
                if (throwable != null) {
                    Timber.e(throwable, "Problem occurred while connecting to the broker "
                            + "at: $brokerAddress:$brokerPort")
                    EncryptedSharedPreferencesUtils.setBrokerState(MqttClientState.DISCONNECTED.toString(), context)
                    TODO("Retry depending on reason")
                } else {
                    Timber.d("Successfully connected to the following broker: "
                            + "$brokerAddress:$brokerPort with ack: $ack")
                    EncryptedSharedPreferencesUtils.setBrokerState(MqttClientState.CONNECTED.toString(), context)
                    TODO("Check the ack to ensure that we are indeed connected")
                }
            }

        return client
    }
}