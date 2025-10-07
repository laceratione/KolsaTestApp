package ru.example.kolsatest.presentation.workoutlist

import ru.example.kolsatest.domain.model.Workout

sealed class WorkoutState {
    class Empty : WorkoutState()
    class Loading : WorkoutState()
    data class Error(val message: String?) : WorkoutState()
    data class Success(val workouts: List<Workout>) : WorkoutState()
}