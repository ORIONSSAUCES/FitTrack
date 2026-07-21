package com.brunoapp.fittrack.di

import com.brunoapp.fittrack.data.repository.DailyLogRepositoryImpl
import com.brunoapp.fittrack.data.repository.DietRepositoryImpl
import com.brunoapp.fittrack.data.repository.ExerciseRepositoryImpl
import com.brunoapp.fittrack.data.repository.FoodRepositoryImpl
import com.brunoapp.fittrack.data.repository.ProfileRepositoryImpl
import com.brunoapp.fittrack.data.repository.RoutineRepositoryImpl
import com.brunoapp.fittrack.data.repository.WorkoutRepositoryImpl
import com.brunoapp.fittrack.domain.repository.DailyLogRepository
import com.brunoapp.fittrack.domain.repository.DietRepository
import com.brunoapp.fittrack.domain.repository.ExerciseRepository
import com.brunoapp.fittrack.domain.repository.FoodRepository
import com.brunoapp.fittrack.domain.repository.ProfileRepository
import com.brunoapp.fittrack.domain.repository.RoutineRepository
import com.brunoapp.fittrack.domain.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(impl: ExerciseRepositoryImpl): ExerciseRepository

    @Binds
    @Singleton
    abstract fun bindRoutineRepository(impl: RoutineRepositoryImpl): RoutineRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(impl: WorkoutRepositoryImpl): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindFoodRepository(impl: FoodRepositoryImpl): FoodRepository

    @Binds
    @Singleton
    abstract fun bindDietRepository(impl: DietRepositoryImpl): DietRepository

    @Binds
    @Singleton
    abstract fun bindDailyLogRepository(impl: DailyLogRepositoryImpl): DailyLogRepository
}
