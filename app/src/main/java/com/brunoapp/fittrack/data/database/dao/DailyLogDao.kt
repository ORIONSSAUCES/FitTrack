package com.brunoapp.fittrack.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.brunoapp.fittrack.data.database.entity.DailyFoodEntryEntity
import com.brunoapp.fittrack.data.database.entity.DailyLogEntity
import com.brunoapp.fittrack.data.database.entity.DailyMealEntity
import com.brunoapp.fittrack.data.database.relation.DailyLogWithMeals
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLogDao {

    @Transaction
    @Query("SELECT * FROM daily_log WHERE date = :date LIMIT 1")
    fun observeLogByDate(date: String): Flow<DailyLogWithMeals?>

    @Transaction
    @Query("SELECT * FROM daily_log WHERE date = :date LIMIT 1")
    suspend fun getLogByDate(date: String): DailyLogWithMeals?

    @Transaction
    @Query("SELECT * FROM daily_log WHERE date BETWEEN :startDate AND :endDate ORDER BY date")
    fun observeLogsBetween(startDate: String, endDate: String): Flow<List<DailyLogWithMeals>>

    @Insert
    suspend fun insertLog(log: DailyLogEntity): Long

    @Query("UPDATE daily_log SET adherence = :adherence WHERE id = :logId")
    suspend fun setAdherence(logId: Long, adherence: String)

    @Query("UPDATE daily_log SET isTrainingDay = :isTraining WHERE id = :logId")
    suspend fun setTrainingDay(logId: Long, isTraining: Boolean)

    @Insert
    suspend fun insertMeal(meal: DailyMealEntity): Long

    @Query("UPDATE daily_meal SET isCompleted = :completed WHERE id = :mealId")
    suspend fun setMealCompleted(mealId: Long, completed: Boolean)

    @Query("DELETE FROM daily_meal WHERE id = :mealId")
    suspend fun deleteMeal(mealId: Long)

    @Insert
    suspend fun insertEntry(entry: DailyFoodEntryEntity): Long

    @Query("UPDATE daily_food_entry SET quantity = :quantity WHERE id = :entryId")
    suspend fun updateEntryQuantity(entryId: Long, quantity: Double)

    @Query("DELETE FROM daily_food_entry WHERE id = :entryId")
    suspend fun deleteEntry(entryId: Long)
}
