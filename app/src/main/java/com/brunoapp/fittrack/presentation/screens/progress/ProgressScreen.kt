package com.brunoapp.fittrack.presentation.screens.progress

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brunoapp.fittrack.R
import com.brunoapp.fittrack.core.utils.DateUtils
import com.brunoapp.fittrack.domain.model.BodyMeasurement
import com.brunoapp.fittrack.domain.model.ProgressPhoto
import com.brunoapp.fittrack.presentation.components.FileImage
import com.brunoapp.fittrack.presentation.components.SimpleLineChart

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = hiltViewModel()
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.progress_tab_weight),
        stringResource(R.string.progress_tab_measurements),
        stringResource(R.string.progress_tab_photos)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        when (selectedTab) {
            0 -> WeightTab(viewModel)
            1 -> MeasurementsTab(viewModel)
            2 -> PhotosTab(viewModel)
        }
    }
}

// ══════════════════ WEIGHT ══════════════════

@Composable
private fun WeightTab(viewModel: ProgressViewModel) {
    val stats by viewModel.weightStats.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Stats cards
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WeightStatCard(
                    value = stats.current?.let { "%.1f".format(it) } ?: "—",
                    label = stringResource(R.string.weight_current),
                    modifier = Modifier.weight(1f)
                )
                WeightStatCard(
                    value = stats.weeklyAverage?.let { "%.1f".format(it) } ?: "—",
                    label = stringResource(R.string.weight_week_avg),
                    modifier = Modifier.weight(1f)
                )
                WeightStatCard(
                    value = stats.totalChange?.let {
                        (if (it >= 0) "+" else "") + "%.1f".format(it)
                    } ?: "—",
                    label = stringResource(R.string.weight_total_change),
                    modifier = Modifier.weight(1f)
                )
            }

            // Week vs previous week
            if (stats.weeklyAverage != null && stats.previousWeekAverage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                val diff = stats.weeklyAverage!! - stats.previousWeekAverage!!
                Text(
                    text = stringResource(
                        R.string.weight_vs_prev_week,
                        (if (diff >= 0) "+" else "") + "%.1f".format(diff)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Chart
            if (stats.chartValues.size >= 2) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = stringResource(R.string.weight_chart_title),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SimpleLineChart(
                            values = stats.chartValues,
                            minLabel = "%.1f kg".format(stats.chartValues.min()),
                            maxLabel = "%.1f kg".format(stats.chartValues.max())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Entries list
            stats.entries.forEach { entry ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "%.1f kg".format(entry.weightKg),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${entry.date}  ${entry.time}" +
                                    if (entry.notes.isNotBlank()) "  ·  ${entry.notes}" else "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { viewModel.onDeleteWeight(entry.id) }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.action_delete),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.weight_add))
        }
    }

    if (showAddDialog) {
        var weightText by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }
        var error by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(R.string.weight_add)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it; error = false },
                        label = { Text(stringResource(R.string.weight_kg)) },
                        singleLine = true,
                        isError = error,
                        supportingText = if (error) {
                            { Text(stringResource(R.string.error_invalid_weight)) }
                        } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text(stringResource(R.string.weight_notes_hint)) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (viewModel.onSaveWeight(weightText, notes)) showAddDialog = false
                        else error = true
                    }
                ) { Text(stringResource(R.string.action_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun WeightStatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ══════════════════ MEASUREMENTS ══════════════════

@Composable
private fun MeasurementsTab(viewModel: ProgressViewModel) {
    val measurements by viewModel.measurements.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (measurements.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.measurement_empty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(measurements, key = { it.id }) { m ->
                    MeasurementCard(m, onDelete = { viewModel.onDeleteMeasurement(m.id) })
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.measurement_add))
        }
    }

    if (showAddDialog) {
        MeasurementDialog(
            onSave = { fields, notes ->
                if (viewModel.onSaveMeasurement(fields, notes)) {
                    showAddDialog = false
                    true
                } else false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun MeasurementCard(m: BodyMeasurement, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = m.date,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            val parts = buildList {
                m.waistCm?.let { add("Cintura ${fmt(it)}") }
                m.abdomenCm?.let { add("Abdomen ${fmt(it)}") }
                m.chestCm?.let { add("Pecho ${fmt(it)}") }
                m.hipsCm?.let { add("Cadera ${fmt(it)}") }
                m.neckCm?.let { add("Cuello ${fmt(it)}") }
                m.leftArmCm?.let { add("Brazo izq ${fmt(it)}") }
                m.rightArmCm?.let { add("Brazo der ${fmt(it)}") }
                m.leftThighCm?.let { add("Muslo izq ${fmt(it)}") }
                m.rightThighCm?.let { add("Muslo der ${fmt(it)}") }
                m.bodyFatPct?.let { add("Grasa ${fmt(it)}%") }
            }
            Text(
                text = parts.joinToString("  ·  "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun fmt(v: Double): String =
    if (v % 1.0 == 0.0) "${v.toInt()} cm".replace(" cm%", "%")
    else "%.1f cm".format(v)

@Composable
private fun MeasurementDialog(
    onSave: (Map<String, String>, String) -> Boolean,
    onDismiss: () -> Unit
) {
    val fieldKeys = listOf(
        "waist" to "Cintura (cm)", "abdomen" to "Abdomen (cm)",
        "chest" to "Pecho (cm)", "hips" to "Cadera (cm)", "neck" to "Cuello (cm)",
        "leftArm" to "Brazo izq (cm)", "rightArm" to "Brazo der (cm)",
        "leftThigh" to "Muslo izq (cm)", "rightThigh" to "Muslo der (cm)",
        "bodyFat" to "Grasa corporal (%)"
    )
    val values = remember { mutableStateOf(fieldKeys.associate { it.first to "" }) }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.measurement_add)) },
        text = {
            Column(
                modifier = Modifier
                    .height(360.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                fieldKeys.chunked(2).forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pair.forEach { (key, label) ->
                            OutlinedTextField(
                                value = values.value[key].orEmpty(),
                                onValueChange = {
                                    values.value = values.value + (key to it)
                                    error = false
                                },
                                label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(stringResource(R.string.exercise_notes)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (error) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.measurement_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (!onSave(values.value, notes)) error = true }
            ) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

// ══════════════════ PHOTOS ══════════════════

@Composable
private fun PhotosTab(viewModel: ProgressViewModel) {
    val photos by viewModel.photos.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showCompare by remember { mutableStateOf(false) }
    var photoToDelete by remember { mutableStateOf<ProgressPhoto?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (photos.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.photo_empty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.photo_privacy_note),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
            if (photos.size >= 2) {
                OutlinedButton(
                    onClick = { showCompare = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                ) {
                    Icon(Icons.Filled.Compare, contentDescription = null)
                    Spacer(modifier = Modifier.height(0.dp))
                    Text("  " + stringResource(R.string.photo_compare_title))
                }
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(photos, key = { it.id }) { photo ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = photo.date +
                                        (photo.weightKg?.let { "  ·  %.1f kg".format(it) } ?: ""),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { photoToDelete = photo }) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(
                                    photo.frontPhotoPath to stringResource(R.string.photo_front),
                                    photo.sidePhotoPath to stringResource(R.string.photo_side),
                                    photo.backPhotoPath to stringResource(R.string.photo_back)
                                ).forEach { (path, label) ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        if (path != null) {
                                            FileImage(
                                                path = path,
                                                contentDescription = label,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(140.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                            )
                                        } else {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(140.dp)
                                            ) {
                                                Text(
                                                    "—",
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.photo_add))
        }
    }

    if (showCompare) {
        PhotoCompareDialog(
            photos = photos,
            onDismiss = { showCompare = false }
        )
    }

    if (showAddDialog) {
        AddPhotoDialog(
            onSave = { weight, front, side, back, notes ->
                viewModel.onSavePhotoEntry(weight, front, side, back, notes)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    photoToDelete?.let { photo ->
        AlertDialog(
            onDismissRequest = { photoToDelete = null },
            title = { Text(stringResource(R.string.photo_delete_title)) },
            text = { Text(stringResource(R.string.photo_delete_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDeletePhotoEntry(photo.id)
                        photoToDelete = null
                    }
                ) {
                    Text(
                        stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { photoToDelete = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun AddPhotoDialog(
    onSave: (String, Uri?, Uri?, Uri?, String) -> Unit,
    onDismiss: () -> Unit
) {
    var weightText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var frontUri by remember { mutableStateOf<Uri?>(null) }
    var sideUri by remember { mutableStateOf<Uri?>(null) }
    var backUri by remember { mutableStateOf<Uri?>(null) }

    val frontPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> frontUri = uri }
    val sidePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> sideUri = uri }
    val backPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> backUri = uri }

    fun pickRequest() = PickVisualMediaRequest(
        ActivityResultContracts.PickVisualMedia.ImageOnly
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.photo_add)) },
        text = {
            Column {
                PhotoSlotButton(
                    label = stringResource(R.string.photo_front),
                    picked = frontUri != null,
                    onClick = { frontPicker.launch(pickRequest()) }
                )
                PhotoSlotButton(
                    label = stringResource(R.string.photo_side),
                    picked = sideUri != null,
                    onClick = { sidePicker.launch(pickRequest()) }
                )
                PhotoSlotButton(
                    label = stringResource(R.string.photo_back),
                    picked = backUri != null,
                    onClick = { backPicker.launch(pickRequest()) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text(stringResource(R.string.photo_weight_optional)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(stringResource(R.string.exercise_notes)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(weightText, frontUri, sideUri, backUri, notes) },
                enabled = frontUri != null || sideUri != null || backUri != null
            ) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
private fun PhotoSlotButton(label: String, picked: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        if (picked) {
            Icon(
                Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(0.dp))
            Text("  ")
        }
        Text(label)
    }
}
