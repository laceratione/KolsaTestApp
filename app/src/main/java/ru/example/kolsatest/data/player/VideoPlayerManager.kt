package ru.example.kolsatest.data.player

import android.content.Context
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector

private const val TAG = "VideoPlayerManager"

class VideoPlayerManager(
    private val context: Context,
) {
    private var exoPlayer: ExoPlayer? = null
    private var trackSelector: DefaultTrackSelector? = null

    var isPlayingBeforeConfigChange = false
        private set

    var isPausedByUser: Boolean = false
        private set

    fun init(): ExoPlayer {
        if (exoPlayer != null) return exoPlayer!!

        trackSelector = DefaultTrackSelector(context)
        exoPlayer = ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector!!)
            .build()


        return exoPlayer!!.apply {
            Log.d(TAG, "Player.Listener init")
//            addListener(object : Player.Listener {
//                override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
//                    if (!playWhenReady) {
//                        // Пауза
//                        isPausedByUser = true
//                    } else {
//                        // Воспроизведение
//                        isPausedByUser = false
//                    }
//                }
//            })
        }
    }

    fun getPlayer(): ExoPlayer? = exoPlayer

    fun setVideoUrl(url: String) {
        try {
            exoPlayer?.apply {
                val mediaItem = MediaItem.fromUri(url)
                setMediaItem(mediaItem)
                prepare()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in setVideoUrl", e)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        exoPlayer?.playbackParameters = PlaybackParameters(speed)
    }

    fun getCurrentSpeed(): Float = exoPlayer?.playbackParameters?.speed ?: 1.0f

    fun pause() {
        try {
            isPlayingBeforeConfigChange = exoPlayer?.isPlaying == true
//            isPausedByUser = false
            if (exoPlayer?.isPlaying == true)
                exoPlayer?.pause()
        } catch (e: Exception) {
            Log.e(TAG, "Error in pause", e)
        }
    }

    /*
    при сворачивании пауза, при возвращении пауза
    при смене конф после возвращения пауза, если юзер паузил
                                     плей, если юзер не паузил
     */
    fun play() {
        try {
            if (isPlayingBeforeConfigChange && !isPausedByUser){
//                isPausedByUser = false
                exoPlayer?.play()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in play", e)
        }
    }

    fun playOrPause(){
        if (exoPlayer?.isPlaying == true){
            isPausedByUser = true
            exoPlayer?.pause()
        } else {
            isPausedByUser = false
            exoPlayer?.play()
        }
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
        trackSelector = null
    }
}