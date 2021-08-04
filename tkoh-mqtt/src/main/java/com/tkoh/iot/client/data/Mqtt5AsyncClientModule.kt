package com.tkoh.iot.client.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
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

const val VALID_PORT_LEFT_ENDPOINT = 1
const val VALID_PORT_RIGHT_ENDPOINT = 65535
const val PORT_NOT_SET = -1

@Module
@InstallIn(SingletonComponent::class)
object Mqtt5AsyncClientModule {

    @Provides
    fun provideMqtt5AsyncClient(
        @ApplicationContext context: Context
    ) : Mqtt5AsyncClient? {
        val encryptedSharedPreferences = getEncryptedSharedPreferences(context)
        val brokerAddress = getBrokerAddress(encryptedSharedPreferences, context)
        val brokerPort = getBrokerPort(encryptedSharedPreferences, context)

        return if (brokerAddress != null && brokerPort != PORT_NOT_SET
            && brokerPort >= VALID_PORT_LEFT_ENDPOINT && brokerPort <= VALID_PORT_RIGHT_ENDPOINT) {
            createClient(brokerAddress, brokerPort, context)
        } else {
            return Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .buildAsync()
        }
    }

    private fun getBrokerAddress(sharedPreferences: SharedPreferences, context: Context) : String? {
        return sharedPreferences.getString(context.getString(R.string.broker_address), null)
    }

    private fun getBrokerPort(sharedPreferences: SharedPreferences, context: Context) : Int {
        return sharedPreferences.getInt(context.getString(R.string.broker_port), PORT_NOT_SET)
    }

    private fun createClient(brokerAddress : String, brokerPort : Int,
            context: Context) : Mqtt5AsyncClient? {
        val client : Mqtt5AsyncClient = Mqtt5Client.builder()
            .identifier(UUID.randomUUID().toString())
            .serverHost(brokerAddress)
            .serverPort(brokerPort)
            .buildAsync()

        client.connect()
            .whenComplete { ack, throwable ->
                if (throwable != null) {
                    Timber.e(throwable, "Problem occurred while connecting to the broker")
                    clearSelectedBrokerPreferences(context)
                } else {
                    Timber.d("Successfully connected to the following broker: " +
                            "$brokerAddress:$brokerPort with ack: $ack")
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

    private fun clearSelectedBrokerPreferences(context: Context) {
        val sharedPreferences: SharedPreferences = getEncryptedSharedPreferences(context)

        sharedPreferences.edit().remove(context.getString(R.string.broker_address)).apply()
        sharedPreferences.edit().remove(context.getString(R.string.broker_port)).apply()
    }
}