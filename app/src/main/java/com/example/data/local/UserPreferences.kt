package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.core.i18n.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rimvo_prefs")

data class UserPrefsSnapshot(
    val onboardingCompleted: Boolean = false,
    val isLoggedIn: Boolean = false,
    val phoneNumber: String = "",
    val jwtToken: String? = null,
    val language: AppLanguage = AppLanguage.EN,
    val creditLimitTotalBdt: Int = 25000,
    val whatsappRemindersEnabled: Boolean = true,
    val pushRemindersEnabled: Boolean = true,
    val pinLockEnabled: Boolean = false,
    val appPin: String = "1234",
    val deliveryAddress: String = "House 14, Road 7, Dhanmondi, Dhaka 1205"
)

class UserPreferences(private val context: Context) {
    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val PHONE_NUMBER = stringPreferencesKey("phone_number")
        val JWT_TOKEN = stringPreferencesKey("jwt_token")
        val LANGUAGE = stringPreferencesKey("language")
        val CREDIT_LIMIT_TOTAL = intPreferencesKey("credit_limit_total")
        val WHATSAPP_REMINDERS = booleanPreferencesKey("whatsapp_reminders")
        val PUSH_REMINDERS = booleanPreferencesKey("push_reminders")
        val PIN_LOCK_ENABLED = booleanPreferencesKey("pin_lock_enabled")
        val APP_PIN = stringPreferencesKey("app_pin")
        val DELIVERY_ADDRESS = stringPreferencesKey("delivery_address")
    }

    val prefsFlow: Flow<UserPrefsSnapshot> = context.dataStore.data.map { prefs ->
        val langCode = prefs[Keys.LANGUAGE] ?: AppLanguage.EN.code
        UserPrefsSnapshot(
            onboardingCompleted = prefs[Keys.ONBOARDING_DONE] ?: false,
            isLoggedIn = prefs[Keys.IS_LOGGED_IN] ?: false,
            phoneNumber = prefs[Keys.PHONE_NUMBER] ?: "",
            jwtToken = prefs[Keys.JWT_TOKEN],
            language = if (langCode == AppLanguage.BN.code) AppLanguage.BN else AppLanguage.EN,
            creditLimitTotalBdt = prefs[Keys.CREDIT_LIMIT_TOTAL] ?: 25000,
            whatsappRemindersEnabled = prefs[Keys.WHATSAPP_REMINDERS] ?: true,
            pushRemindersEnabled = prefs[Keys.PUSH_REMINDERS] ?: true,
            pinLockEnabled = prefs[Keys.PIN_LOCK_ENABLED] ?: false,
            appPin = prefs[Keys.APP_PIN] ?: "1234",
            deliveryAddress = prefs[Keys.DELIVERY_ADDRESS]
                ?: "House 14, Road 7, Dhanmondi, Dhaka 1205"
        )
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language.code }
    }

    suspend fun completeOnboarding() {
        context.dataStore.edit { it[Keys.ONBOARDING_DONE] = true }
    }

    suspend fun saveAuthSession(phoneNumber: String, token: String) {
        context.dataStore.edit {
            it[Keys.ONBOARDING_DONE] = true
            it[Keys.IS_LOGGED_IN] = true
            it[Keys.PHONE_NUMBER] = phoneNumber
            it[Keys.JWT_TOKEN] = token
        }
    }

    suspend fun signOut() {
        context.dataStore.edit {
            it[Keys.ONBOARDING_DONE] = false
            it[Keys.IS_LOGGED_IN] = false
            it.remove(Keys.JWT_TOKEN)
        }
    }

    suspend fun setCreditLimitTotal(limitBdt: Int) {
        context.dataStore.edit { it[Keys.CREDIT_LIMIT_TOTAL] = limitBdt }
    }

    suspend fun setWhatsappReminders(enabled: Boolean) {
        context.dataStore.edit { it[Keys.WHATSAPP_REMINDERS] = enabled }
    }

    suspend fun setPushReminders(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PUSH_REMINDERS] = enabled }
    }

    suspend fun setPinLock(enabled: Boolean, pin: String) {
        context.dataStore.edit {
            it[Keys.PIN_LOCK_ENABLED] = enabled
            it[Keys.APP_PIN] = pin
        }
    }

    suspend fun setDeliveryAddress(address: String) {
        context.dataStore.edit { it[Keys.DELIVERY_ADDRESS] = address }
    }
}
