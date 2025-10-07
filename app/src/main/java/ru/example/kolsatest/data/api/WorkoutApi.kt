package ru.example.kolsatest.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import ru.example.kolsatest.data.model.VideoDTO
import ru.example.kolsatest.data.model.WorkoutDTO

interface WorkoutApi {
    @GET("get_workouts")
    suspend fun getWorkouts(): List<WorkoutDTO>

    @GET("get_video")
    suspend fun getVideoById(
        @Query("id") id: Int,
    ): VideoDTO
}