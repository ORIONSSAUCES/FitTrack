package com.brunoapp.fittrack.di

import com.brunoapp.fittrack.data.repository.ExerciseRepositoryImpl
import com.brunoapp.fittrack.data.repository.ProfileRepositoryImpl
import com.brunoapp.fittrack.domain.repository.ExerciseRepository
import com.brunoapp.fittrack.domain.repository.ProfileRepository
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
}
