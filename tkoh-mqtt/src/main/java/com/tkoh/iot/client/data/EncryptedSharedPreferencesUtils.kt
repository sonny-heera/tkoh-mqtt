package com.tkoh.iot.client.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.tkoh.iot.client.R

class EncryptedSharedPreferencesUtils {

    companion object {

        private lateinit var sharedPreferences : SharedPreferences

        fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
            if (!this::sharedPreferences.isInitialized) {
                sharedPreferences = EncryptedSharedPreferences.create(
                    context.getString(R.string.pref_file),
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
            }

            return sharedPreferences
        }

        fun getBrokerAddress(context: Context) : String? {
            return getEncryptedSharedPreferences(context).getString(context.getString(R.string.broker_address), null)
        }

        fun getBrokerPort(context: Context) : Int {
            return getEncryptedSharedPreferences(context).getInt(context.getString(R.string.broker_port), PORT_NOT_SET)
        }

        fun getBrokerState(context: Context) : String? {
            return getEncryptedSharedPreferences(context)
                .getString(context.getString(R.string.broker_state), null)
        }

        fun setBrokerAddress(address: String, context: Context) {
            return getEncryptedSharedPreferences(context)
                .edit()
                .putString(context.getString(R.string.broker_address), address)
                .apply()
        }

        fun setBrokerPort(port: Int, context: Context) {
            return getEncryptedSharedPreferences(context)
                .edit()
                .putInt(context.getString(R.string.broker_port), port)
                .apply()
        }

        fun setBrokerState(state: String, context: Context) {
            return getEncryptedSharedPreferences(context)
                .edit()
                .putString(context.getString(R.string.broker_state), state)
                .apply()
        }
    }
}