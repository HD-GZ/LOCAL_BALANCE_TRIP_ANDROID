package live.lb_trip.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.fitness.FitnessLocal
import com.google.android.gms.fitness.LocalRecordingClient
import com.google.android.gms.fitness.data.LocalDataType
import com.google.android.gms.fitness.data.LocalField
import com.google.android.gms.fitness.request.LocalDataReadRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import live.lb_trip.domain.model.RecordedMovement
import live.lb_trip.domain.repository.DistanceRecorder
import live.lb_trip.domain.util.suspendRunCatching

class LocalFitnessDistanceRecorder @Inject constructor(
    @ApplicationContext private val context: Context,
) : DistanceRecorder {
    private val client by lazy { FitnessLocal.getLocalRecordingClient(context) }

    @SuppressLint("MissingPermission")
    override suspend fun subscribe(): Result<Unit> = suspendRunCatching {
        val availability = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context, LocalRecordingClient.LOCAL_RECORDING_CLIENT_MIN_VERSION_CODE)
        check(availability == ConnectionResult.SUCCESS) {
            "Google Play services too old for the Recording API: $availability"
        }
        client.subscribe(LocalDataType.TYPE_DISTANCE_DELTA).await()
        client.subscribe(LocalDataType.TYPE_STEP_COUNT_DELTA).await()
        Unit
    }.onFailure { error -> Log.w(TAG, "subscribe failed", error) }

    override suspend fun readMovement(startEpochMillis: Long, endEpochMillis: Long): Result<RecordedMovement> =
        suspendRunCatching {
            val request = LocalDataReadRequest.Builder()
                .read(LocalDataType.TYPE_DISTANCE_DELTA)
                .read(LocalDataType.TYPE_STEP_COUNT_DELTA)
                .setTimeRange(startEpochMillis, endEpochMillis, TimeUnit.MILLISECONDS)
                .build()
            val response = client.readData(request).await()
            val distanceMeters = response.dataSets
                .filter { it.dataType == LocalDataType.TYPE_DISTANCE_DELTA }
                .flatMap { it.dataPoints }
                .sumOf { it.getValue(LocalField.FIELD_DISTANCE).asFloat().toDouble() }
                .toFloat()
            val stepCount = response.dataSets
                .filter { it.dataType == LocalDataType.TYPE_STEP_COUNT_DELTA }
                .flatMap { it.dataPoints }
                .sumOf { it.getValue(LocalField.FIELD_STEPS).asInt() }
            RecordedMovement(distanceMeters = distanceMeters, stepCount = stepCount)
        }

    override suspend fun unsubscribe(): Result<Unit> {
        val distanceResult = unsubscribeWithRetry(LocalDataType.TYPE_DISTANCE_DELTA)
        val stepsResult = unsubscribeWithRetry(LocalDataType.TYPE_STEP_COUNT_DELTA)
        return distanceResult.mapCatching { stepsResult.getOrThrow() }
    }

    private suspend fun unsubscribeWithRetry(dataType: LocalDataType): Result<Unit> {
        var result = suspendRunCatching { client.unsubscribe(dataType).await(); Unit }
        repeat(UNSUBSCRIBE_RETRY_ATTEMPTS - 1) {
            if (result.isSuccess) return result
            delay(UNSUBSCRIBE_RETRY_DELAY_MILLIS)
            result = suspendRunCatching { client.unsubscribe(dataType).await(); Unit }
        }
        return result
    }
}

private const val TAG = "LocalFitnessDistanceRecorder"
private const val UNSUBSCRIBE_RETRY_ATTEMPTS = 3
private const val UNSUBSCRIBE_RETRY_DELAY_MILLIS = 500L
