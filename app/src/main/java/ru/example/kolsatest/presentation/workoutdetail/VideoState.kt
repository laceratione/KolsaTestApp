package ru.example.kolsatest.presentation.workoutdetail

import ru.example.kolsatest.domain.model.Video

sealed class VideoState {
    class Loading : VideoState()
    data class Error(val message: String?) : VideoState()
    data class Success(val video: Video) : VideoState()
}