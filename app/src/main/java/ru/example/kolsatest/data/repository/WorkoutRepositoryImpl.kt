package ru.example.kolsatest.data.repository

import ru.example.kolsatest.data.api.WorkoutApi
import ru.example.kolsatest.data.model.mapToDomain
import ru.example.kolsatest.domain.model.Video
import ru.example.kolsatest.domain.model.Workout
import ru.example.kolsatest.domain.repository.WorkoutRepository
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private var workoutApi: WorkoutApi,
) : WorkoutRepository {
    override suspend fun getWorkouts(): Result<List<Workout>> {
        return try {
            val workouts = workoutApi.getWorkouts().map { it.mapToDomain() }
            Result.success(workouts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getVideoById(id: Int): Result<Video> {
        return try {
            val video = workoutApi.getVideoById(id).mapToDomain()
            Result.success(video)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}