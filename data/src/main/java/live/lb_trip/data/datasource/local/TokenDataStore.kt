package live.lb_trip.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import live.lb_trip.data.util.EncryptionManager
import live.lb_trip.domain.model.Tokens
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val encryptionManager: EncryptionManager,
) {
    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")

    val tokens: Flow<Tokens?> = dataStore.data.map { prefs ->
        val accessToken = prefs[accessTokenKey]
        val refreshToken = prefs[refreshTokenKey]
        if (accessToken != null && refreshToken != null) {
            Tokens(
                accessToken = encryptionManager.decrypt(accessToken),
                refreshToken = encryptionManager.decrypt(refreshToken),
            )
        } else {
            null
        }
    }

    suspend fun save(accessToken: String, refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[accessTokenKey] = encryptionManager.encrypt(accessToken)
            prefs[refreshTokenKey] = encryptionManager.encrypt(refreshToken)
        }
    }

    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(accessTokenKey)
            prefs.remove(refreshTokenKey)
        }
    }
}
