package com.brunoapp.fittrack.presentation.screens.nutrition.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.domain.model.DietPlan
import com.brunoapp.fittrack.domain.model.FoodItem
import com.brunoapp.fittrack.domain.model.Recipe
import com.brunoapp.fittrack.domain.repository.DietRepository
import com.brunoapp.fittrack.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class PickerState(
    val mealId: Long,
    val section: Int = 0,          // 0 = foods, 1 = recipes
    val query: String = ""
)

@HiltViewModel
class DietPlanViewModel @Inject constructor(
    private val dietRepository: DietRepository,
    foodRepository: FoodRepository
) : ViewModel() {

    val activePlan: StateFlow<DietPlan?> = dietRepository.observeActivePlan()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val allFoods: StateFlow<List<FoodItem>> = foodRepository.observeFoods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allRecipes: StateFlow<List<Recipe>> = foodRepository.observeRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Selected weekday, defaults to today. */
    private val _selectedDay = MutableStateFlow(LocalDate.now().dayOfWeek.value - 1)
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()

    private val _picker = MutableStateFlow<PickerState?>(null)
    val picker: StateFlow<PickerState?> = _picker.asStateFlow()

    fun onSelectDay(day: Int) {
        _selectedDay.value = day.coerceIn(0, 6)
    }

    fun onToggleTrainingDay(dayId: Long, current: Boolean) {
        viewModelScope.launch { dietRepository.setTrainingDay(dayId, !current) }
    }

    fun onAddMeal(dayId: Long, name: String, order: Int) {
        if (name.isBlank()) return
        viewModelScope.launch { dietRepository.addMeal(dayId, name.trim(), order) }
    }

    fun onDeleteMeal(mealId: Long) {
        viewModelScope.launch { dietRepository.deleteMeal(mealId) }
    }

    fun onShowPicker(mealId: Long) {
        _picker.value = PickerState(mealId = mealId)
    }

    fun onDismissPicker() {
        _picker.value = null
    }

    fun onPickerSection(section: Int) {
        _picker.value = _picker.value?.copy(section = section)
    }

    fun onPickerQuery(query: String) {
        _picker.value = _picker.value?.copy(query = query)
    }

    fun onPickFood(food: FoodItem) {
        val picker = _picker.value ?: return
        viewModelScope.launch {
            dietRepository.addFoodItem(picker.mealId, food.id, food.servingSize)
            _picker.value = null
        }
    }

    fun onPickRecipe(recipe: Recipe) {
        val picker = _picker.value ?: return
        viewModelScope.launch {
            dietRepository.addRecipeItem(picker.mealId, recipe.id, 1.0)
            _picker.value = null
        }
    }

    fun onItemQuantityChange(itemId: Long, quantity: Double) {
        viewModelScope.launch {
            dietRepository.updateItemQuantity(itemId, quantity.coerceIn(0.0, 10_000.0))
        }
    }

    fun onDeleteItem(itemId: Long) {
        viewModelScope.launch { dietRepository.deleteItem(itemId) }
    }

    fun onCreatePlan(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val id = dietRepository.createPlan(name.trim())
            dietRepository.setActivePlan(id)
        }
    }
}
