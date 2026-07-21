package com.brunoapp.fittrack.presentation.screens.nutrition.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.domain.model.FoodItem
import com.brunoapp.fittrack.domain.model.Recipe
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

data class FoodFilterState(
    val query: String = "",
    val favoritesOnly: Boolean = false
)

@HiltViewModel
class FoodLibraryViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(FoodFilterState())
    val filter: StateFlow<FoodFilterState> = _filter.asStateFlow()

    val foods: StateFlow<List<FoodItem>> =
        combine(repository.observeFoods(), _filter) { all, filter ->
            all.filter { food ->
                val matchesQuery = filter.query.isBlank() ||
                    food.name.lowercase().contains(filter.query.trim().lowercase()) ||
                    food.brand.lowercase().contains(filter.query.trim().lowercase())
                val matchesFavorite = !filter.favoritesOnly || food.isFavorite
                matchesQuery && matchesFavorite
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val recipes: StateFlow<List<Recipe>> =
        combine(repository.observeRecipes(), _filter) { all, filter ->
            all.filter { recipe ->
                filter.query.isBlank() ||
                    recipe.name.lowercase().contains(filter.query.trim().lowercase())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onQueryChange(query: String) {
        _filter.value = _filter.value.copy(query = query)
    }

    fun onToggleFavoritesOnly() {
        _filter.value = _filter.value.copy(favoritesOnly = !_filter.value.favoritesOnly)
    }

    fun onToggleFavorite(food: FoodItem) {
        viewModelScope.launch { repository.setFoodFavorite(food.id, !food.isFavorite) }
    }

    fun onDeleteFood(food: FoodItem) {
        viewModelScope.launch { repository.deleteFood(food.id) }
    }

    fun onDeleteRecipe(recipe: Recipe) {
        viewModelScope.launch { repository.deleteRecipe(recipe.id) }
    }
}
