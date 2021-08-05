package com.tkoh.iot.client.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.hivemq.client.mqtt.MqttClientState
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import com.tkoh.iot.client.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import java.util.UUID

const val ADDRESS_NOT_SET = "Broker address not set"
const val PORT_NOT_SET = 0

@Module
@InstallIn(SingletonComponent::class)
object Mqtt5AsyncClientModule {

    @Provides
    fun provideMqtt5AsyncClient(
        @ApplicationContext context: Context
    ) : Mqtt5AsyncClient {
        val brokerAddress = getBrokerAddress(context)
        val brokerPort = getBrokerPort(context)

        return createClient(brokerAddress, brokerPort, context)
    }

    private fun getBrokerAddress(context: Context) : String? {
        return getEncryptedSharedPreferences(context)
            .getString(context.getString(R.string.broker_address), null)
    }

    private fun getBrokerPort(context: Context) : Int {
        return getEncryptedSharedPreferences(context)
            .getInt(context.getString(R.string.broker_port), PORT_NOT_SET)
    }

    private fun createClient(brokerAddress : String?, brokerPort : Int,
            context: Context) : Mqtt5AsyncClient {
        return if (brokerAddress != null && brokerPort != PORT_NOT_SET) {
            buildAndConnectClient(brokerAddress, brokerPort, context)
        } else {
            getEncryptedSharedPreferences(context).edit()
                .putString(context.getString(R.string.broker_state),
                    MqttClientState.DISCONNECTED.toString()).apply()

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
        val sharedPreferences = getEncryptedSharedPreferences(context)

        val client = Mqtt5Client.builder()
            .identifier(UUID.randomUUID().toString())
            .serverHost(brokerAddress)
            .serverPort(brokerPort)
            .buildAsync()

        sharedPreferences.edit().putString(context.getString(R.string.broker_state),
            MqttClientState.CONNECTING.toString()).apply()

        client.connect()
            .whenComplete { ack, throwable ->
                if (throwable != null) {
                    Timber.e(throwable, "Problem occurred while connecting to the broker")
                    sharedPreferences.edit().putString(context.getString(R.string.broker_state),
                        MqttClientState.DISCONNECTED.toString()).apply()
                    TODO("Retry depending on reason")
                } else {
                    Timber.d("Successfully connected to the following broker: "
                            + "$brokerAddress:$brokerPort with ack: $ack")
                    sharedPreferences.edit().putString(context.getString(R.string.broker_state),
                        MqttClientState.CONNECTED.toString()).apply()
                    TODO("Check the ack to ensure that we are indeed connected")
                }
            }

        return client
    }

    private fun getEncryptedSharedPreferences(context: Context) : SharedPreferences {
        return EncryptedSharedPreferences.create(
            context.getString(R.string.pref_file),
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}