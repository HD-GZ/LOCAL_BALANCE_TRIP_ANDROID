package live.lb_trip.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import live.lb_trip.data.di.qualifier.TourDistanceDataStore
import live.lb_trip.domain.repository.DistanceRecordingStore

@Singleton
class TourRecordingSessionLocalDataSource @Inject constructor(
    @TourDistanceDataStore private val dataStore: DataStore<Preferences>,
) : DistanceRecordingStore {

    override suspend fun saveStart(savedCourseId: Long, startEpochMillis: Long) {
        dataStore.edit { prefs -> prefs[recordingStartKey(savedCourseId)] = startEpochMillis }
    }

    override suspend fun getStart(savedCourseId: Long): Long? =
        dataStore.data.first()[recordingStartKey(savedCourseId)]

    override suspend fun clearStart(savedCourseId: Long) {
        dataStore.edit { prefs -> prefs.remove(recordingStartKey(savedCourseId)) }
    }

    override suspend fun hasOtherActiveStart(excludingSavedCourseId: Long): Boolean {
        val excludedKeyName = recordingStartKey(excludingSavedCourseId).name
        return dataStore.data.first().asMap().keys.any { key ->
            key.name.startsWith(RECORDING_START_KEY_PREFIX) && key.name != excludedKeyName
        }
    }

    override suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }

    private fun recordingStartKey(savedCourseId: Long) =
        longPreferencesKey("$RECORDING_START_KEY_PREFIX$savedCourseId")
}

private const val RECORDING_START_KEY_PREFIX = "recording_start_"
