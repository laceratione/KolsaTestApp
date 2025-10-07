package ru.example.kolsatest.data.model

import com.google.gson.annotations.SerializedName
import ru.example.kolsatest.domain.model.Workout

data class WorkoutDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("type") val type: Int,
    @SerializedName("duration") val duration: String,
)

fun WorkoutDTO.mapToDomain() = Workout(
    id = this.id,
    title = this.title,
    description = this.description,
    type = this.type,
    duration = this.duration,
)