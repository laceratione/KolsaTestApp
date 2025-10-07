package ru.example.kolsatest.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.example.kolsatest.data.repository.WorkoutRepositoryImpl
import ru.example.kolsatest.domain.repository.WorkoutRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindWorkoutRepositoryImpl(workoutRepositoryImpl: WorkoutRepositoryImpl): WorkoutRepository
}