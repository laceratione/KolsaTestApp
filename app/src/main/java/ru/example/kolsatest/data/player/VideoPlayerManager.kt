package ru.example.kolsatest.data.player

import android.content.Context
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector

private const val TAG = "VideoPlayerManager"

class VideoPlayerManager(
    private val context: Context,
) {
    private var exoPlayer: ExoPlayer? = null
    private var trackSelector: DefaultTrackSelector? = null

    fun init(): ExoPlayer {
        if (exoPlayer != null) return exoPlayer!!

        trackSelector = DefaultTrackSelector(context)
        exoPlayer = ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector!!)
            .build()

        return exoPlayer!!
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
            if (exoPlayer?.isPlaying == true)
                exoPlayer?.pause()
        } catch (e: Exception) {
            Log.e(TAG, "Error in pause", e)
        }
    }

    fun play() {
        try {
            exoPlayer?.play()
        } catch (e: Exception){
            Log.e(TAG, "Error in play", e)
        }
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
        trackSelector = null
    }
}