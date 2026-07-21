package com.brunoapp.fittrack.data.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.brunoapp.fittrack.data.database.entity.DailyFoodEntryEntity
import com.brunoapp.fittrack.data.database.entity.DailyLogEntity
import com.brunoapp.fittrack.data.database.entity.DailyMealEntity

data class DailyMealWithEntries(
    @Embedded val meal: DailyMealEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "dailyMealId"
    )
    val entries: List<DailyFoodEntryEntity>
)

data class DailyLogWithMeals(
    @Embedded val log: DailyLogEntity,
    @Relation(
        entity = DailyMealEntity::class,
        parentColumn = "id",
        entityColumn = "dailyLogId"
    )
    val meals: List<DailyMealWithEntries>
)
