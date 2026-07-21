package com.brunoapp.fittrack.di

import android.content.Context
import androidx.room.Room
import com.brunoapp.fittrack.data.database.FitTrackDatabase
import com.brunoapp.fittrack.data.database.dao.ActiveWorkoutStateDao
import com.brunoapp.fittrack.data.database.dao.BackupDao
import com.brunoapp.fittrack.data.database.dao.DailyLogDao
import com.brunoapp.fittrack.data.database.dao.DietDao
import com.brunoapp.fittrack.data.database.dao.ExerciseDao
import com.brunoapp.fittrack.data.database.dao.FoodDao
import com.brunoapp.fittrack.data.database.dao.PersonalRecordDao
import com.brunoapp.fittrack.data.database.dao.ProfileDao
import com.brunoapp.fittrack.data.database.dao.ProgressDao
import com.brunoapp.fittrack.data.database.dao.RoutineDao
import com.brunoapp.fittrack.data.database.dao.WorkoutDao
import com.brunoapp.fittrack.data.database.migration.MIGRATION_1_2
import com.brunoapp.fittrack.data.database.migration.MIGRATION_2_3
import com.brunoapp.fittrack.data.database.migration.MIGRATION_3_4
import com.brunoapp.fittrack.data.database.migration.MIGRATION_4_5
import com.brunoapp.fittrack.data.database.migration.MIGRATION_5_6
import com.brunoapp.fittrack.data.database.migration.MIGRATION_6_7
import com.brunoapp.fittrack.data.database.migration.MIGRATION_7_8
import com.brunoapp.fittrack.data.database.migration.MIGRATION_8_9
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
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
            .build()

    @Provides
    fun provideProfileDao(db: FitTrackDatabase): ProfileDao = db.profileDao()

    @Provides
    fun provideExerciseDao(db: FitTrackDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    fun providePersonalRecordDao(db: FitTrackDatabase): PersonalRecordDao =
        db.personalRecordDao()

    @Provides
    fun provideRoutineDao(db: FitTrackDatabase): RoutineDao = db.routineDao()

    @Provides
    fun provideWorkoutDao(db: FitTrackDatabase): WorkoutDao = db.workoutDao()

    @Provides
    fun provideActiveWorkoutStateDao(db: FitTrackDatabase): ActiveWorkoutStateDao =
        db.activeWorkoutStateDao()

    @Provides
    fun provideFoodDao(db: FitTrackDatabase): FoodDao = db.foodDao()

    @Provides
    fun provideDietDao(db: FitTrackDatabase): DietDao = db.dietDao()

    @Provides
    fun provideDailyLogDao(db: FitTrackDatabase): DailyLogDao = db.dailyLogDao()

    @Provides
    fun provideProgressDao(db: FitTrackDatabase): ProgressDao = db.progressDao()

    @Provides
    fun provideBackupDao(db: FitTrackDatabase): BackupDao = db.backupDao()
}
