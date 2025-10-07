package ru.example.kolsatest.domain.model

import androidx.annotation.StringRes
import ru.example.kolsatest.R
import java.io.Serializable

enum class WorkoutType(val id: Int, @StringRes val idRes: Int) {
    CARDIO(1, R.string.cardio),
    STRENGTH(2, R.string.strength),
    STRETCHING(3, R.string.stretching);

    companion object {
        fun fromId(id: Int): WorkoutType? {
            return entries.find { it.id == id }
        }
    }
}

data class Workout(
    val id: Int,
    val title: String,
    val description: String?,
    val type: Int,
    val duration: String,
) : Serializable