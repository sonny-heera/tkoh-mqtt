package com.tkoh.iot.client.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.tkoh.iot.client.R
import com.tkoh.iot.client.data.EncryptedSharedPreferencesUtils
import com.tkoh.iot.client.data.Mqtt5AsyncClientModule
import com.tkoh.iot.client.databinding.SelectedBrokerFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectedBrokerFragment : Fragment(R.layout.selected_broker_fragment) {
    @Inject lateinit var client: Mqtt5AsyncClient

    private lateinit var binding: SelectedBrokerFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SelectedBrokerFragmentBinding.inflate(layoutInflater)

        binding.connectButton.setOnClickListener {
            if (!binding.selectedBrokerAddress.text.isEmpty()
                && binding.selectedBrokerPort.text.isDigitsOnly()) {
                val address = binding.selectedBrokerAddress.text.toString()
                val port = Integer.parseInt(binding.selectedBrokerPort.text.toString())

                if (port in 1..65535) {
                    EncryptedSharedPreferencesUtils.setBrokerAddress(address, requireContext())
                    EncryptedSharedPreferencesUtils.setBrokerPort(port, requireContext())
                    client = Mqtt5AsyncClientModule.provideMqtt5AsyncClient(requireContext())
                }
            }
        }

        return binding.root
    }
}