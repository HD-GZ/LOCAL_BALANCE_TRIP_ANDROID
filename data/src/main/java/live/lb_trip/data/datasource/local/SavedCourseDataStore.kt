package live.lb_trip.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import live.lb_trip.data.di.qualifier.SavedCourses
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedCourseDataStore @Inject constructor(
    @SavedCourses private val dataStore: DataStore<Preferences>,
) {
    private val savedCourseIdsKey = stringSetPreferencesKey("saved_course_ids")

    val savedCourseIds: Flow<Set<Long>> = dataStore.data.map { prefs ->
        prefs[savedCourseIdsKey]?.mapNotNull(String::toLongOrNull)?.toSet() ?: emptySet()
    }

    suspend fun markSaved(courseId: Long) {
        dataStore.edit { prefs ->
            val current = prefs[savedCourseIdsKey] ?: emptySet()
            prefs[savedCourseIdsKey] = current + courseId.toString()
        }
    }
}
