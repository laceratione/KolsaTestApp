package ru.example.kolsatest.presentation.workoutdetail

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.example.kolsatest.data.player.VideoPlayerManager
import ru.example.kolsatest.domain.model.Video
import ru.example.kolsatest.domain.model.Workout
import ru.example.kolsatest.domain.repository.WorkoutRepository
import javax.inject.Inject

private const val TAG = "WorkoutDetailsViewModel"

@HiltViewModel
class WorkoutDetailsViewModel @Inject constructor(
    private val repository: WorkoutRepository,
) : ViewModel() {
    private val _video = MutableStateFlow<VideoState>(VideoState.Loading())
    val video: StateFlow<VideoState> = _video.asStateFlow()

    var workout: Workout? = null
        private set
    private lateinit var videoPlayerManager: VideoPlayerManager
    var currentUrl: String = ""
        private set
    val speedsValue = floatArrayOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)

    fun setWorkout(workout: Workout){
        this.workout = workout
    }

    fun initVideoPlayer(context: Context) {
        if (!::videoPlayerManager.isInitialized) {
            videoPlayerManager = VideoPlayerManager(context)
            videoPlayerManager.init()
        }
    }

    fun loadVideoByWorkoutId() {
        workout?.let {
            viewModelScope.launch {
                try {
                    if (_video.value is VideoState.Success) return@launch

                    val result = repository.getVideoById(it.id)
                    when {
                        result.isSuccess -> {
                            _video.value = VideoState.Success(result.getOrNull() ?: Video())
                        }

                        result.isFailure -> {
                            val error = result.exceptionOrNull()?.message
                            VideoState.Error(error)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in loadVideoByWorkoutId", e)
                }
            }
        } ?: Log.e(TAG, "workout null")
    }

    fun setVideoUrl(url: String) {
        currentUrl = url
        videoPlayerManager.setVideoUrl(url)
    }

    fun getPlayer(): ExoPlayer? = videoPlayerManager.getPlayer()

    fun setPlaybackSpeed(speed: Float) {
        videoPlayerManager.setPlaybackSpeed(speed)
    }

    fun getCurrentSpeed(): Float = videoPlayerManager.getCurrentSpeed()

    fun pause() {
        videoPlayerManager.pause()
    }

    fun pauseOnHideApp() {
        videoPlayerManager.getPlayer()?.pause()
    }

    fun play() {
        videoPlayerManager.play()
    }

    fun playOrPause(){
        videoPlayerManager.playOrPause()
    }

    override fun onCleared() {
        if (::videoPlayerManager.isInitialized) {
            videoPlayerManager.release()
        }
        super.onCleared()
    }
}