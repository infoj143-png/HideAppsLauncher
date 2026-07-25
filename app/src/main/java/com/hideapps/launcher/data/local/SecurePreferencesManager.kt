package com.hideapps.launcher.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "secure_launcher_settings")

@Singleton
class SecurePreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val pinKey = stringPreferencesKey("secure_pin")

    suspend fun savePin(pin: String) {
        val encrypted = KeyStoreHelper.encrypt(pin)
        context.dataStore.edit { preferences ->
            preferences[pinKey] = encrypted
        }
    }

    suspend fun getPin(): String? {
        val encrypted = context.dataStore.data.map { preferences ->
            preferences[pinKey]
        }.firstOrNull() ?: return null

        return try {
            KeyStoreHelper.decrypt(encrypted)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun clearPin() {
        context.dataStore.edit { preferences ->
            preferences.remove(pinKey)
        }
    }
}
