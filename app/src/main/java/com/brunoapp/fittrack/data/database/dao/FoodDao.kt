package com.brunoapp.fittrack.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.brunoapp.fittrack.data.database.entity.FoodItemEntity
import com.brunoapp.fittrack.data.database.entity.RecipeEntity
import com.brunoapp.fittrack.data.database.entity.RecipeIngredientEntity
import com.brunoapp.fittrack.data.database.relation.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

    // ── Foods ──

    @Query("SELECT * FROM food_item ORDER BY name COLLATE NOCASE")
    fun observeAllFoods(): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_item WHERE id = :id")
    suspend fun getFood(id: Long): FoodItemEntity?

    @Query("SELECT * FROM food_item")
    suspend fun getAllFoodsOnce(): List<FoodItemEntity>

    @Query("SELECT COUNT(*) FROM food_item")
    suspend fun countFoods(): Int

    @Upsert
    suspend fun upsertFood(food: FoodItemEntity): Long

    @Upsert
    suspend fun upsertFoods(foods: List<FoodItemEntity>)

    @Query("DELETE FROM food_item WHERE id = :id")
    suspend fun deleteFood(id: Long)

    @Query("UPDATE food_item SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFoodFavorite(id: Long, favorite: Boolean)

    @Query("UPDATE food_item SET lastUsed = :timestamp WHERE id = :id")
    suspend fun touchFood(id: Long, timestamp: String)

    // ── Recipes ──

    @Transaction
    @Query("SELECT * FROM recipe ORDER BY name COLLATE NOCASE")
    fun observeAllRecipes(): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query("SELECT * FROM recipe WHERE id = :id")
    suspend fun getRecipe(id: Long): RecipeWithIngredients?

    @Query("SELECT COUNT(*) FROM recipe")
    suspend fun countRecipes(): Int

    @Insert
    suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Query("DELETE FROM recipe WHERE id = :id")
    suspend fun deleteRecipe(id: Long)

    @Insert
    suspend fun insertIngredients(ingredients: List<RecipeIngredientEntity>)

    @Query("DELETE FROM recipe_ingredient WHERE recipeId = :recipeId")
    suspend fun deleteIngredientsForRecipe(recipeId: Long)

    @Transaction
    suspend fun saveFullRecipe(
        recipe: RecipeEntity,
        ingredients: List<RecipeIngredientEntity>
    ): Long {
        val recipeId: Long
        if (recipe.id == 0L) {
            recipeId = insertRecipe(recipe)
        } else {
            updateRecipe(recipe)
            recipeId = recipe.id
            deleteIngredientsForRecipe(recipeId)
        }
        insertIngredients(ingredients.map { it.copy(id = 0, recipeId = recipeId) })
        return recipeId
    }
}
