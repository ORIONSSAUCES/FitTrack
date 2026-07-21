package com.brunoapp.fittrack.presentation.screens.progress

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.core.utils.Validators
import com.brunoapp.fittrack.domain.model.BodyMeasurement
import com.brunoapp.fittrack.domain.model.ProgressPhoto
import com.brunoapp.fittrack.domain.model.WeightEntry
import com.brunoapp.fittrack.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class WeightStats(
    val entries: List<WeightEntry> = emptyList(),
    val current: Double? = null,
    val weeklyAverage: Double? = null,
    val previousWeekAverage: Double? = null,
    val totalChange: Double? = null,          // vs first entry
    val chartValues: List<Double> = emptyList() // oldest → newest
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val repository: ProgressRepository
) : ViewModel() {

    private val today: LocalDate = LocalDate.now()

    val weightStats: StateFlow<WeightStats> = repository.observeWeights()
        .map { entries ->
            // entries come newest-first
            val chronological = entries.sortedWith(
                compareBy({ it.date }, { it.time })
            )
            val currentWeek = today.minusDays(6).toString()
            val prevWeekStart = today.minusDays(13).toString()
            val thisWeek = chronological.filter { it.date >= currentWeek }
            val prevWeek = chronological.filter { it.date in prevWeekStart..currentWeek }
                .filter { it.date < currentWeek }

            WeightStats(
                entries = entries,
                current = chronological.lastOrNull()?.weightKg,
                weeklyAverage = thisWeek.map { it.weightKg }.ifEmpty { null }?.average(),
                previousWeekAverage = prevWeek.map { it.weightKg }.ifEmpty { null }?.average(),
                totalChange = if (chronological.size >= 2)
                    chronological.last().weightKg - chronological.first().weightKg
                else null,
                chartValues = chronological.map { it.weightKg }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WeightStats())

    val measurements: StateFlow<List<BodyMeasurement>> = repository.observeMeasurements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val photos: StateFlow<List<ProgressPhoto>> = repository.observePhotos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── Weight actions ──

    /** Returns false when input is invalid. */
    fun onSaveWeight(weightText: String, notes: String): Boolean {
        val weight = Validators.parseBodyWeight(weightText).getOrNull() ?: return false
        viewModelScope.launch {
            repository.saveWeight(
                WeightEntry(
                    date = today.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
                    weightKg = weight,
                    notes = notes.trim()
                )
            )
        }
        return true
    }

    fun onDeleteWeight(id: Long) {
        viewModelScope.launch { repository.deleteWeight(id) }
    }

    // ── Measurement actions ──

    fun onSaveMeasurement(fields: Map<String, String>, notes: String): Boolean {
        fun parse(key: String): Double? =
            fields[key]?.trim()?.takeIf { it.isNotBlank() }
                ?.replace(',', '.')?.toDoubleOrNull()

        val values = listOf(
            "waist", "abdomen", "chest", "hips", "neck",
            "leftArm", "rightArm", "leftThigh", "rightThigh", "bodyFat"
        ).associateWith { parse(it) }

        // invalid if a non-blank field failed to parse
        val invalid = fields.any { (key, text) ->
            text.isNotBlank() && values[key] == null && key in values.keys
        }
        if (invalid) return false
        if (values.values.all { it == null }) return false

        viewModelScope.launch {
            repository.saveMeasurement(
                BodyMeasurement(
                    date = today.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    waistCm = values["waist"],
                    abdomenCm = values["abdomen"],
                    chestCm = values["chest"],
                    hipsCm = values["hips"],
                    neckCm = values["neck"],
                    leftArmCm = values["leftArm"],
                    rightArmCm = values["rightArm"],
                    leftThighCm = values["leftThigh"],
                    rightThighCm = values["rightThigh"],
                    bodyFatPct = values["bodyFat"],
                    notes = notes.trim()
                )
            )
        }
        return true
    }

    fun onDeleteMeasurement(id: Long) {
        viewModelScope.launch { repository.deleteMeasurement(id) }
    }

    // ── Photo actions ──

    fun onSavePhotoEntry(weightText: String, front: Uri?, side: Uri?, back: Uri?, notes: String) {
        val weight = weightText.trim().takeIf { it.isNotBlank() }
            ?.replace(',', '.')?.toDoubleOrNull()
        viewModelScope.launch {
            repository.savePhotoEntry(
                date = today.format(DateTimeFormatter.ISO_LOCAL_DATE),
                weightKg = weight,
                frontUri = front,
                sideUri = side,
                backUri = back,
                notes = notes.trim()
            )
        }
    }

    fun onDeletePhotoEntry(id: Long) {
        viewModelScope.launch { repository.deletePhotoEntry(id) }
    }
}
