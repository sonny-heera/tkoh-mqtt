package com.tkoh.iot.client.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.hivemq.client.mqtt.MqttClientState
import com.tkoh.iot.client.R
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

private const val PREF_FILE_KEY = "pref_file"
private const val BROKER_ADDRESS_KEY = "broker_address"
private const val BROKER_PORT_KEY = "broker_port"
private const val BROKER_STATE_KEY = "broker_state"

class Mqtt5AsyncClientModuleTest {

    @MockK private lateinit var sharedPreferences : SharedPreferences
    @MockK private lateinit var context : Context
    private var editor : SharedPreferences.Editor = spyk()

    private var mqtt5AsyncClientModule = Mqtt5AsyncClientModule

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `return connecting client when values are present in SharedPreferences`() {
        val validBrokerAddress = "validBrokerAddress"
        val validBrokerPort = 23

        mockSharedPreferencesValues(validBrokerAddress, validBrokerPort)

        val client = Mqtt5AsyncClientModule.provideMqtt5AsyncClient(context)

        assertNotNull(client)

        verify(exactly = 1) { editor.putString(BROKER_STATE_KEY, MqttClientState.CONNECTING.toString()) }
        verify(exactly = 1) { editor.apply() }

        confirmVerified(editor)

        assertEquals(client.config.state, MqttClientState.CONNECTING)
        assertEquals(client.config.serverHost, validBrokerAddress)
        assertEquals(client.config.serverPort, validBrokerPort)
    }

    @Test
    fun `return disconnected client when broker address is not set, but port is`() {
        val addressNot = null
        val validBrokerPort = 23

        mockSharedPreferencesValues(addressNot, validBrokerPort)

        val client = mqtt5AsyncClientModule.provideMqtt5AsyncClient(context)

        assertNotNull(client)

        verify(exactly = 1) { editor.putString(BROKER_STATE_KEY, MqttClientState.DISCONNECTED.toString()) }
        verify(exactly = 1) { editor.apply() }

        confirmVerified(editor)

        assertEquals(client.config.state, MqttClientState.DISCONNECTED)
    }

    @Test
    fun `return disconnected client when broker port is not set, but address is`() {
        val validBrokerAddress = "validBrokerAddress"
        val unsetPort = PORT_NOT_SET

        mockSharedPreferencesValues(validBrokerAddress, unsetPort)

        val client = mqtt5AsyncClientModule.provideMqtt5AsyncClient(context)

        assertNotNull(client)

        verify(exactly = 1) { editor.putString(BROKER_STATE_KEY, MqttClientState.DISCONNECTED.toString()) }
        verify(exactly = 1) { editor.apply() }

        confirmVerified(editor)

        assertEquals(client.config.state, MqttClientState.DISCONNECTED)
    }

    @Test
    fun `return disconnected client when broker port and address are not set`() {
        val addressNotSet = null
        val unsetPort = PORT_NOT_SET

        mockSharedPreferencesValues(addressNotSet, unsetPort)

        val client = mqtt5AsyncClientModule.provideMqtt5AsyncClient(context)

        assertNotNull(client)

        verify(exactly = 1) { editor.putString(BROKER_STATE_KEY, MqttClientState.DISCONNECTED.toString()) }
        verify(exactly = 1) { editor.apply() }

        confirmVerified(editor)

        assertEquals(client.config.state, MqttClientState.DISCONNECTED)
    }

    private fun mockSharedPreferencesValues(brokerAddress: String?, brokerPort: Int) {
        mockkStatic(EncryptedSharedPreferences::class)
        every { EncryptedSharedPreferences.create(any(), any(), any(), any(), any()) } returns sharedPreferences

        every { context.getString(R.string.pref_file) } returns PREF_FILE_KEY
        every { context.getString(R.string.broker_address) } returns BROKER_ADDRESS_KEY
        every { context.getString(R.string.broker_port) } returns BROKER_PORT_KEY
        every { context.getString(R.string.broker_state) } returns BROKER_STATE_KEY

        every { sharedPreferences.getString(BROKER_ADDRESS_KEY, any()) } returns brokerAddress
        every { sharedPreferences.getInt(BROKER_PORT_KEY, any()) } returns brokerPort
        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.apply() } returns

        mockkStatic(MasterKeys::class)
        every { MasterKeys.getOrCreate(any()) } returns ""
    }
}