package live.lb_trip.core.util

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val TAG = "AudioGuidePlayer"
private const val PROGRESS_POLL_INTERVAL_MILLIS = 200L

/**
 * Plays one audio-guide track at a time via Media3 [ExoPlayer]. Tapping the same [toggle] url
 * pauses/resumes in place (position retained); a different url releases the previous player
 * and starts fresh. Not thread-safe beyond the main thread a ViewModel normally calls from.
 */
class AudioGuidePlayer @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var exoPlayer: ExoPlayer? = null
    private var progressJob: Job? = null
    private var currentUrl: String? = null

    fun toggle(
        url: String,
        scope: CoroutineScope,
        onState: (isPlaying: Boolean, positionMs: Int, durationMs: Int) -> Unit,
        onCompletion: () -> Unit,
    ) {
        val player = exoPlayer
        if (currentUrl == url && player != null) {
            if (player.isPlaying) {
                progressJob?.cancel()
                player.pause()
                onState(false, player.currentPosition.toInt(), player.duration.coerceAtLeast(0).toInt())
            } else {
                player.play()
                startProgressLoop(scope, onState)
            }
            return
        }
        startFresh(url, scope, onState, onCompletion)
    }

    private fun startFresh(
        url: String,
        scope: CoroutineScope,
        onState: (isPlaying: Boolean, positionMs: Int, durationMs: Int) -> Unit,
        onCompletion: () -> Unit,
    ) {
        release()
        currentUrl = url
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            progressJob?.cancel()
                            onCompletion()
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        Log.e(TAG, "playback error url=$url", error)
                        progressJob?.cancel()
                        onCompletion()
                    }
                },
            )
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = true
        }
        startProgressLoop(scope, onState)
    }

    private fun startProgressLoop(scope: CoroutineScope, onState: (Boolean, Int, Int) -> Unit) {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                val player = exoPlayer ?: break
                runCatching {
                    onState(player.isPlaying, player.currentPosition.toInt(), player.duration.coerceAtLeast(0).toInt())
                }
                delay(PROGRESS_POLL_INTERVAL_MILLIS.milliseconds)
            }
        }
    }

    fun release() {
        progressJob?.cancel()
        progressJob = null
        runCatching { exoPlayer?.release() }
        exoPlayer = null
        currentUrl = null
    }
}

fun formatAudioPosition(positionMs: Int, durationMs: Int): String {
    fun format(ms: Int): String {
        val totalSeconds = ms / 1000
        return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
    }
    return "${format(positionMs)} / ${format(durationMs)}"
}
