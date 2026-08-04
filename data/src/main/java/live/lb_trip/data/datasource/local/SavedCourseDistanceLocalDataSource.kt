package live.lb_trip.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import live.lb_trip.data.di.qualifier.TourDistanceDataStore

@Singleton
class SavedCourseDistanceLocalDataSource @Inject constructor(
    @TourDistanceDataStore private val dataStore: DataStore<Preferences>,
) {
    suspend fun save(savedCourseId: Long, distanceMeters: Float) {
        dataStore.edit { prefs -> prefs[distanceKey(savedCourseId)] = distanceMeters }
    }

    suspend fun get(savedCourseId: Long): Float? = dataStore.data.first()[distanceKey(savedCourseId)]

    suspend fun saveSteps(savedCourseId: Long, stepCount: Int) {
        dataStore.edit { prefs -> prefs[stepCountKey(savedCourseId)] = stepCount }
    }

    suspend fun getSteps(savedCourseId: Long): Int? = dataStore.data.first()[stepCountKey(savedCourseId)]

    private fun distanceKey(savedCourseId: Long) = floatPreferencesKey("distance_meters_$savedCourseId")

    private fun stepCountKey(savedCourseId: Long) = intPreferencesKey("step_count_$savedCourseId")
}
