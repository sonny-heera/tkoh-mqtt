package com.tkoh.iot.client.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tkoh.iot.client.data.MqttClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BrokerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    mqttClientRepository: MqttClientRepository
) : ViewModel() {
}