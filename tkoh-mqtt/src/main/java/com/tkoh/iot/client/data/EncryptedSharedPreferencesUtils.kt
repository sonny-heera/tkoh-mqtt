package com.tkoh.iot.client.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.tkoh.iot.client.R

class EncryptedSharedPreferencesUtils {

    companion object {
        fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
            return EncryptedSharedPreferences.create(
                context.getString(R.string.pref_file),
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }

        fun getBrokerAddress(context: Context) : String? {
            return getEncryptedSharedPreferences(context)
                .getString(context.getString(R.string.broker_address), null)
        }

        fun getBrokerPort(context: Context) : Int {
            return getEncryptedSharedPreferences(context)
                .getInt(context.getString(R.string.broker_port), PORT_NOT_SET)
        }

        fun setBrokerAddress(address: String, context: Context) {
            getEncryptedSharedPreferences(context)
                .edit()
                .putString(context.getString(R.string.broker_address), address)
                .apply()
        }

        fun setBrokerPort(port: Int, context: Context) {
            getEncryptedSharedPreferences(context)
                .edit()
                .putInt(context.getString(R.string.broker_port), port)
                .apply()
        }
    }
}