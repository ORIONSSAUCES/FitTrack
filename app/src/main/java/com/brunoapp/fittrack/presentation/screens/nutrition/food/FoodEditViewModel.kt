package com.brunoapp.fittrack.presentation.screens.nutrition.food

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.domain.model.FoodItem
import com.brunoapp.fittrack.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FoodEditUiState(
    val name: String = "",
    val brand: String = "",
    val caloriesText: String = "",
    val proteinText: String = "",
    val carbsText: String = "",
    val fatText: String = "",
    val fiberText: String = "",
    val servingText: String = "100",
    val servingUnit: String = "g",
    val notes: String = "",
    val nameError: Boolean = false,
    val numbersError: Boolean = false,
    val isSaved: Boolean = false,
    val isEditMode: Boolean = false,
    val isCustom: Boolean = true
)

@HiltViewModel
class FoodEditViewModel @Inject constructor(
    private val repository: FoodRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val foodId: Long = savedStateHandle["foodId"] ?: -1L

    private val _uiState = MutableStateFlow(FoodEditUiState())
    val uiState: StateFlow<FoodEditUiState> = _uiState.asStateFlow()

    init {
        if (foodId > 0) {
            viewModelScope.launch {
                repository.getFood(foodId)?.let { food ->
                    _uiState.value = FoodEditUiState(
                        name = food.name,
                        brand = food.brand,
                        caloriesText = food.caloriesPer100.clean(),
                        proteinText = food.proteinPer100.clean(),
                        carbsText = food.carbsPer100.clean(),
                        fatText = food.fatPer100.clean(),
                        fiberText = food.fiberPer100.clean(),
                        servingText = food.servingSize.clean(),
                        servingUnit = food.servingUnit,
                        notes = food.notes,
                        isEditMode = true,
                        isCustom = food.isCustom
                    )
                }
            }
        }
    }

    fun onNameChange(v: String) = update { it.copy(name = v, nameError = false) }
    fun onBrandChange(v: String) = update { it.copy(brand = v) }
    fun onCaloriesChange(v: String) = update { it.copy(caloriesText = v, numbersError = false) }
    fun onProteinChange(v: String) = update { it.copy(proteinText = v, numbersError = false) }
    fun onCarbsChange(v: String) = update { it.copy(carbsText = v, numbersError = false) }
    fun onFatChange(v: String) = update { it.copy(fatText = v, numbersError = false) }
    fun onFiberChange(v: String) = update { it.copy(fiberText = v, numbersError = false) }
    fun onServingChange(v: String) = update { it.copy(servingText = v, numbersError = false) }
    fun onUnitChange(v: String) = update { it.copy(servingUnit = v) }
    fun onNotesChange(v: String) = update { it.copy(notes = v) }

    fun onSaveClick() {
        val s = _uiState.value
        if (s.name.isBlank()) {
            update { it.copy(nameError = true) }
            return
        }
        val calories = s.caloriesText.parseNonNegative()
        val protein = s.proteinText.parseNonNegative()
        val carbs = s.carbsText.parseNonNegative()
        val fat = s.fatText.parseNonNegative()
        val fiber = s.fiberText.parseNonNegative()
        val serving = s.servingText.parseNonNegative()

        if (calories == null || protein == null || carbs == null ||
            fat == null || fiber == null || serving == null || serving <= 0.0
        ) {
            update { it.copy(numbersError = true) }
            return
        }

        viewModelScope.launch {
            repository.saveFood(
                FoodItem(
                    id = if (foodId > 0) foodId else 0,
                    name = s.name.trim(),
                    brand = s.brand.trim(),
                    caloriesPer100 = calories,
                    proteinPer100 = protein,
                    carbsPer100 = carbs,
                    fatPer100 = fat,
                    fiberPer100 = fiber,
                    servingSize = serving,
                    servingUnit = s.servingUnit,
                    notes = s.notes.trim(),
                    isCustom = if (foodId > 0) s.isCustom else true
                )
            )
            update { it.copy(isSaved = true) }
        }
    }

    private fun update(transform: (FoodEditUiState) -> FoodEditUiState) {
        _uiState.value = transform(_uiState.value)
    }
}

private fun Double.clean(): String =
    if (this % 1.0 == 0.0) toInt().toString() else toString()

private fun String.parseNonNegative(): Double? {
    if (isBlank()) return 0.0
    val value = trim().replace(',', '.').toDoubleOrNull() ?: return null
    return if (value >= 0.0) value else null
}
