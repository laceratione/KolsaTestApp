package ru.example.kolsatest.data.model

import com.google.gson.annotations.SerializedName
import ru.example.kolsatest.domain.model.Video

data class VideoDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("duration") val duration: String,
    @SerializedName("link") val link: String,
)

fun VideoDTO.mapToDomain() = Video(
    id = this.id,
    duration = this.duration,
    link = this.link,
)