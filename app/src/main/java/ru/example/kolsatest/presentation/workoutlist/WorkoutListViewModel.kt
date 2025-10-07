package ru.example.kolsatest.presentation.workoutlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.example.kolsatest.domain.model.Workout
import ru.example.kolsatest.domain.model.WorkoutType
import ru.example.kolsatest.domain.repository.WorkoutRepository
import javax.inject.Inject

private const val TAG = "WorkoutListViewModel"

@HiltViewModel
class WorkoutListViewModel @Inject constructor(
    private val repository: WorkoutRepository,
) : ViewModel() {
    private val _workouts: MutableStateFlow<WorkoutState> = MutableStateFlow(WorkoutState.Loading())
    val workouts: StateFlow<WorkoutState> = _workouts.asStateFlow()

    private val _workoutsOriginal = mutableListOf<Workout>()
    private val _workoutsByQuery = mutableListOf<Workout>()
    var currentFilter: WorkoutType? = null
        private set

    init {
        loadWorkouts()
    }

    fun loadWorkouts() {
        viewModelScope.launch {
            try {
                _workouts.value = WorkoutState.Loading()
                val result = repository.getWorkouts()
                when {
                    result.isSuccess -> {
                        val data = result.getOrDefault(emptyList())
                        _workoutsOriginal.clear()
                        _workoutsByQuery.clear()
                        _workoutsOriginal.addAll(data)
                        _workoutsByQuery.addAll(data)
                        _workouts.value = WorkoutState.Success(data)
                    }

                    result.isFailure -> {
                        val error = result.exceptionOrNull()?.message
                        WorkoutState.Error(error)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in loadWorkouts", e)
            }
        }
    }

    fun search(query: String) {
        try {
            currentFilter = null
            _workoutsByQuery.clear()
            _workoutsByQuery.addAll(_workoutsOriginal.filter {
                it.title.contains(query, ignoreCase = true)
            })
            val newList = _workoutsByQuery.toList()
            _workouts.value = if (newList.isNotEmpty()) {
                WorkoutState.Success(newList)
            } else
                WorkoutState.Empty()
        } catch (e: Exception) {
            Log.e(TAG, "Error in search", e)
        }
    }

    fun filtering(type: WorkoutType) {
        try {
            currentFilter = type
            val workoutByFilter = _workoutsByQuery.filter { it.type == type.id }
            _workouts.value = if (workoutByFilter.isNotEmpty()) {
                WorkoutState.Success(workoutByFilter)
            } else
                WorkoutState.Empty()
        } catch (e: Exception) {
            Log.e(TAG, "Error in filtering", e)
        }
    }

    fun resetFilters() {
        currentFilter = null
        _workouts.value = WorkoutState.Success(_workoutsByQuery.toList())
    }

    fun clearTextSearch() {
        currentFilter = null
        _workoutsByQuery.clear()
        _workoutsByQuery.addAll(_workoutsOriginal.toList())
        _workouts.value = WorkoutState.Success(_workoutsOriginal.toList())
    }
}