package ru.example.kolsatest.domain.repository

import ru.example.kolsatest.domain.model.Video
import ru.example.kolsatest.domain.model.Workout

interface WorkoutRepository {
    suspend fun getWorkouts(): Result<List<Workout>>
    suspend fun getVideoById(id: Int): Result<Video>
}