package com.brunoapp.fittrack.di

import android.content.Context
import androidx.room.Room
import com.brunoapp.fittrack.data.database.FitTrackDatabase
import com.brunoapp.fittrack.data.database.dao.ProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FitTrackDatabase =
        Room.databaseBuilder(
            context,
            FitTrackDatabase::class.java,
            FitTrackDatabase.DATABASE_NAME
        )
            // Migrations are registered here as the schema evolves.
            // .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideProfileDao(db: FitTrackDatabase): ProfileDao = db.profileDao()
}
