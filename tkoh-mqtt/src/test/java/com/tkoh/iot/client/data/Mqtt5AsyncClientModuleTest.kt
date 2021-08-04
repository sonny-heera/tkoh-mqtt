package com.tkoh.iot.client.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.hivemq.client.mqtt.MqttClientState
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class Mqtt5AsyncClientModuleTest {

    private var mqtt5AsyncClientModule = Mqtt5AsyncClientModule
    private val encryptedSharedPreferences = mockk<SharedPreferences>()
    private val context = mockk<Context>()

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `return connecting client when appropriate values are present in SharedPreferences`() {
        val validBrokerAddress = "validBrokerAddress"
        val validBrokerPort = 1

        mockSharedPreferencesValues(validBrokerAddress, validBrokerPort)

        val client = Mqtt5AsyncClientModule.provideMqtt5AsyncClient(context)

        assertNotNull(client)

        assertEquals(client?.config?.state, MqttClientState.CONNECTING)
        assertEquals(client?.config?.serverHost, validBrokerAddress)
        assertEquals(client?.config?.serverPort, validBrokerPort)
    }

    @Test
    fun `return disconnected client when broker address is null`() {
        val invalidBrokerAddress = null
        val validBrokerPort = 1

        mockSharedPreferencesValues(invalidBrokerAddress, validBrokerPort)

        val client = mqtt5AsyncClientModule.provideMqtt5AsyncClient(context)

        assertNotNull(client)

        assertEquals(client?.config?.state, MqttClientState.DISCONNECTED)
    }

    @Test
    fun `return disconnected client when broker port is out of range`() {
        val validBrokerAddress = "validBrokerAddress"
        val invalidBrokerPortLeftEndpoint = VALID_PORT_LEFT_ENDPOINT - 1
        val invalidBrokerPortRightEndpoint = VALID_PORT_RIGHT_ENDPOINT + 1

        // Check left endpoint out-of-range value
        mockSharedPreferencesValues(validBrokerAddress, invalidBrokerPortLeftEndpoint)

        val client1 = mqtt5AsyncClientModule.provideMqtt5AsyncClient(context)

        assertNotNull(client1)

        assertEquals(client1?.config?.state, MqttClientState.DISCONNECTED)

        // Check right endpoint out-of-range value
        mockSharedPreferencesValues(validBrokerAddress, invalidBrokerPortRightEndpoint)

        val client2 = mqtt5AsyncClientModule.provideMqtt5AsyncClient(context)

        assertNotNull(client2)

        assertEquals(client2?.config?.state, MqttClientState.DISCONNECTED)
    }

    private fun mockSharedPreferencesValues(brokerAddress: String?, brokerPort: Int) {
        mockkStatic(EncryptedSharedPreferences::class)
        every { EncryptedSharedPreferences.create(any(), any(), any(), any(), any()) } returns encryptedSharedPreferences

        every { context.getString(any()) } returns ""
        every { encryptedSharedPreferences.getString(any(), any()) } returns brokerAddress

        every { encryptedSharedPreferences.getInt(any(), any()) } returns brokerPort

        mockkStatic(MasterKeys::class)
        every { MasterKeys.getOrCreate(any()) } returns ""
    }
}