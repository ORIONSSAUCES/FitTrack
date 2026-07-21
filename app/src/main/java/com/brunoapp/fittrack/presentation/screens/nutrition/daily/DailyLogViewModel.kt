package com.brunoapp.fittrack.presentation.screens.nutrition.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.core.constants.AdherenceLevel
import com.brunoapp.fittrack.core.utils.ComplianceCalc
import com.brunoapp.fittrack.domain.model.DailyLog
import com.brunoapp.fittrack.domain.model.DietPlan
import com.brunoapp.fittrack.domain.model.FoodItem
import com.brunoapp.fittrack.domain.model.Recipe
import com.brunoapp.fittrack.domain.repository.DailyLogRepository
import com.brunoapp.fittrack.domain.repository.DietRepository
import com.brunoapp.fittrack.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class DailyPickerState(
    val mealId: Long,
    val section: Int = 0,
    val query: String = ""
)

@HiltViewModel
class DailyLogViewModel @Inject constructor(
    private val repository: DailyLogRepository,
    dietRepository: DietRepository,
    foodRepository: FoodRepository
) : ViewModel() {

    private val today: LocalDate = LocalDate.now()
    private val todayString: String = today.format(DateTimeFormatter.ISO_LOCAL_DATE)

    val todayLog: StateFlow<DailyLog?> = repository.observeLog(todayString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val activePlan: StateFlow<DietPlan?> = dietRepository.observeActivePlan()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Weekly compliance from Monday through today. */
    val weeklyCompliance: StateFlow<Int?> = run {
        val monday = today.with(DayOfWeek.MONDAY).format(DateTimeFormatter.ISO_LOCAL_DATE)
        repository.observeLogsBetween(monday, todayString)
            .map { logs ->
                ComplianceCalc.weeklyPercent(
                    logs.map { it.completedMeals to it.totalMeals }
                )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    }

    val allFoods: StateFlow<List<FoodItem>> = foodRepository.observeFoods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allRecipes: StateFlow<List<Recipe>> = foodRepository.observeRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _picker = MutableStateFlow<DailyPickerState?>(null)
    val picker: StateFlow<DailyPickerState?> = _picker.asStateFlow()

    /** True when there is no plan-day to copy (fallback to empty start). */
    private val _startError = MutableStateFlow(false)
    val startError: StateFlow<Boolean> = _startError.asStateFlow()

    fun onStartFromPlan() {
        viewModelScope.launch {
            val dayOfWeek = today.dayOfWeek.value - 1  // 0 = Monday
            val result = repository.startDayFromPlan(todayString, dayOfWeek)
            if (result == null) _startError.value = true
        }
    }

    fun onStartEmpty() {
        viewModelScope.launch { repository.startEmptyDay(todayString) }
    }

    fun onStartErrorShown() {
        _startError.value = false
    }

    fun onToggleMealCompleted(mealId: Long, current: Boolean) {
        viewModelScope.launch { repository.setMealCompleted(mealId, !current) }
    }

    fun onSetAdherence(logId: Long, adherence: AdherenceLevel) {
        viewModelScope.launch { repository.setAdherence(logId, adherence) }
    }

    fun onToggleTrainingDay(logId: Long, current: Boolean) {
        viewModelScope.launch { repository.setTrainingDay(logId, !current) }
    }

    fun onAddMeal(logId: Long, name: String, order: Int) {
        if (name.isBlank()) return
        viewModelScope.launch { repository.addMeal(logId, name.trim(), order) }
    }

    fun onDeleteMeal(mealId: Long) {
        viewModelScope.launch { repository.deleteMeal(mealId) }
    }

    fun onShowPicker(mealId: Long) {
        _picker.value = DailyPickerState(mealId = mealId)
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
            repository.addFoodEntry(picker.mealId, food.id, food.servingSize)
            _picker.value = null
        }
    }

    fun onPickRecipe(recipe: Recipe) {
        val picker = _picker.value ?: return
        viewModelScope.launch {
            repository.addRecipeEntry(picker.mealId, recipe.id, 1.0)
            _picker.value = null
        }
    }

    fun onEntryQuantityChange(entryId: Long, quantity: Double) {
        viewModelScope.launch {
            repository.updateEntryQuantity(entryId, quantity.coerceIn(0.0, 10_000.0))
        }
    }

    fun onDeleteEntry(entryId: Long) {
        viewModelScope.launch { repository.deleteEntry(entryId) }
    }
}
