package com.brunoapp.fittrack.presentation.screens.nutrition.food

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.core.utils.NutritionCalc
import com.brunoapp.fittrack.domain.model.FoodItem
import com.brunoapp.fittrack.domain.model.MacroSummary
import com.brunoapp.fittrack.domain.model.Recipe
import com.brunoapp.fittrack.domain.model.RecipeIngredient
import com.brunoapp.fittrack.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditableIngredient(
    val foodItemId: Long,
    val foodName: String,
    val quantity: Double,
    val unit: String
)

data class RecipeEditUiState(
    val name: String = "",
    val description: String = "",
    val servingsText: String = "1",
    val ingredients: List<EditableIngredient> = emptyList(),
    val nameError: Boolean = false,
    val isSaved: Boolean = false,
    val isEditMode: Boolean = false,
    val showFoodPicker: Boolean = false,
    val pickerQuery: String = ""
)

@HiltViewModel
class RecipeEditViewModel @Inject constructor(
    private val repository: FoodRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recipeId: Long = savedStateHandle["recipeId"] ?: -1L

    private val _uiState = MutableStateFlow(RecipeEditUiState())
    val uiState: StateFlow<RecipeEditUiState> = _uiState.asStateFlow()

    val allFoods: StateFlow<List<FoodItem>> = repository.observeFoods()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /** Live macro totals of the recipe being edited. */
    val totals: StateFlow<MacroSummary> =
        combine(_uiState, allFoods) { state, foods ->
            val foodById = foods.associateBy { it.id }
            NutritionCalc.total(
                state.ingredients.mapNotNull { ingredient ->
                    foodById[ingredient.foodItemId]?.let { it to ingredient.quantity }
                }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MacroSummary()
        )

    init {
        if (recipeId > 0) {
            viewModelScope.launch {
                repository.getRecipe(recipeId)?.let { recipe ->
                    _uiState.value = RecipeEditUiState(
                        name = recipe.name,
                        description = recipe.description,
                        servingsText = recipe.servings.toString(),
                        ingredients = recipe.ingredients.map { ingredient ->
                            EditableIngredient(
                                foodItemId = ingredient.foodItemId,
                                foodName = ingredient.foodName,
                                quantity = ingredient.quantity,
                                unit = ingredient.unit
                            )
                        },
                        isEditMode = true
                    )
                }
            }
        }
    }

    fun onNameChange(v: String) = update { it.copy(name = v, nameError = false) }
    fun onDescriptionChange(v: String) = update { it.copy(description = v) }
    fun onServingsChange(v: String) = update { it.copy(servingsText = v) }

    fun onShowPicker() = update { it.copy(showFoodPicker = true, pickerQuery = "") }
    fun onDismissPicker() = update { it.copy(showFoodPicker = false) }
    fun onPickerQueryChange(v: String) = update { it.copy(pickerQuery = v) }

    fun onAddIngredient(food: FoodItem) = update {
        it.copy(
            ingredients = it.ingredients + EditableIngredient(
                foodItemId = food.id,
                foodName = food.name,
                quantity = food.servingSize,
                unit = food.servingUnit
            ),
            showFoodPicker = false
        )
    }

    fun onQuantityChange(index: Int, value: Double) = update { state ->
        state.copy(
            ingredients = state.ingredients.mapIndexed { i, ingredient ->
                if (i == index) ingredient.copy(quantity = value.coerceIn(0.0, 10_000.0))
                else ingredient
            }
        )
    }

    fun onRemoveIngredient(index: Int) = update {
        it.copy(ingredients = it.ingredients.filterIndexed { i, _ -> i != index })
    }

    fun onSaveClick() {
        val s = _uiState.value
        if (s.name.isBlank()) {
            update { it.copy(nameError = true) }
            return
        }
        val servings = s.servingsText.trim().toIntOrNull()?.coerceIn(1, 50) ?: 1
        viewModelScope.launch {
            repository.saveRecipe(
                Recipe(
                    id = if (recipeId > 0) recipeId else 0,
                    name = s.name.trim(),
                    description = s.description.trim(),
                    servings = servings,
                    ingredients = s.ingredients.map { ingredient ->
                        RecipeIngredient(
                            foodItemId = ingredient.foodItemId,
                            quantity = ingredient.quantity
                        )
                    }
                )
            )
            update { it.copy(isSaved = true) }
        }
    }

    private fun update(transform: (RecipeEditUiState) -> RecipeEditUiState) {
        _uiState.value = transform(_uiState.value)
    }
}
