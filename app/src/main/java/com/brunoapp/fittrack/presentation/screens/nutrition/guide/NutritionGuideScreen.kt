package com.brunoapp.fittrack.presentation.screens.nutrition.guide

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brunoapp.fittrack.core.utils.GuideProgress
import com.brunoapp.fittrack.data.guide.NutritionGuide
import com.brunoapp.fittrack.presentation.theme.GoldRecord

private fun kg(value: Double): String = "%.1f".format(value).replace('.', ',')

@Composable
fun NutritionGuideScreen(
    viewModel: NutritionGuideViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    if (state.loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Which phase is being viewed (defaults to the active one)
    var viewedPhase by rememberSaveable { mutableIntStateOf(state.phase) }
    LaunchedEffect(state.phase) { viewedPhase = state.phase }

    var showWeightDialog by remember { mutableStateOf(false) }
    var showStartPhase2 by remember { mutableStateOf(false) }
    var showBackToPhase1 by remember { mutableStateOf(false) }
    var showNotesDialog by remember { mutableStateOf(false) }

    val phase2Unlocked = state.phase == 2 || state.reachedTarget
    val phase = if (viewedPhase == 2) NutritionGuide.phase2 else NutritionGuide.phase1

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Guía de nutrición",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))
            PhaseSelector(
                viewed = viewedPhase,
                active = state.phase,
                phase2Unlocked = phase2Unlocked,
                onSelect = { viewedPhase = it }
            )
        }

        item {
            WeightProgressCard(
                state = state,
                onLogWeight = { showWeightDialog = true }
            )
        }

        if (state.phase == 1 && state.reachedTarget) {
            item { ReachedTargetBanner(onStart = { showStartPhase2 = true }) }
        }

        if (viewedPhase == 2 && !phase2Unlocked) {
            item { LockedPhaseCard(remainingKg = state.remainingKg) }
        } else {
            item { GoalCard(phase) }
            item { TargetsCard(phase.targets) }
            item { MenusCard(phase.menus) }
            phase.sections.forEachIndexed { index, section ->
                item(key = "${phase.number}-$index") {
                    SectionCard(section = section, initiallyOpen = index == 0)
                }
            }
            item {
                ProtocolNotesCard(notes = state.notes, onEdit = { showNotesDialog = true })
            }
            if (viewedPhase == 2 && state.phase == 2) {
                item {
                    TextButton(onClick = { showBackToPhase1 = true }) {
                        Text("Volver a la Fase 1")
                    }
                }
            }
        }

        item {
            Text(
                text = "Guía orientativa. No reemplaza la indicación ni el seguimiento de tu médico.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showWeightDialog) {
        var text by remember { mutableStateOf("") }
        var error by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { showWeightDialog = false },
            title = { Text("Registrar peso de hoy") },
            text = {
                Column {
                    Text(
                        "En ayunas y después de ir al baño.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it; error = false },
                        label = { Text("Peso (kg)") },
                        singleLine = true,
                        isError = error,
                        supportingText = if (error) { { Text("Peso inválido (30–400 kg)") } } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (viewModel.onLogWeight(text)) showWeightDialog = false else error = true
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showWeightDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showStartPhase2) {
        AlertDialog(
            onDismissRequest = { showStartPhase2 = false },
            title = { Text("¿Iniciar la Fase 2?") },
            text = {
                Text(
                    "Empieza con 2 a 4 semanas de transición: subes calorías de a poco hasta " +
                        "que el peso se estabilice. Planifica con tu médico la suspensión de la tirzepatida."
                )
            },
            confirmButton = {
                Button(onClick = { showStartPhase2 = false; viewModel.onStartPhase2() }) {
                    Text("Iniciar Fase 2")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartPhase2 = false }) { Text("Todavía no") }
            }
        )
    }

    if (showBackToPhase1) {
        AlertDialog(
            onDismissRequest = { showBackToPhase1 = false },
            title = { Text("¿Volver a la Fase 1?") },
            text = { Text("La guía vuelve a mostrar el plan de descenso.") },
            confirmButton = {
                TextButton(onClick = { showBackToPhase1 = false; viewModel.onBackToPhase1() }) {
                    Text("Volver")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackToPhase1 = false }) { Text("Cancelar") }
            }
        )
    }

    if (showNotesDialog) {
        var text by remember { mutableStateOf(state.notes) }
        AlertDialog(
            onDismissRequest = { showNotesDialog = false },
            title = { Text("Mi protocolo") },
            text = {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("Medicación, dosis y días que indicó tu médico, fecha del próximo análisis…") },
                    minLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = { viewModel.onSaveNotes(text); showNotesDialog = false }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotesDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

// ═════════════════════════ Components ═════════════════════════

@Composable
private fun PhaseSelector(viewed: Int, active: Int, phase2Unlocked: Boolean, onSelect: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        listOf(1 to "Fase 1 · Descenso", 2 to "Fase 2 · Volumen").forEach { (number, label) ->
            val selected = viewed == number
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface
                    )
                    .clickable { onSelect(number) }
            ) {
                if (number == 2 && !phase2Unlocked) {
                    Icon(
                        Icons.Filled.Lock, contentDescription = null,
                        tint = if (selected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = label + if (active == number) " ●" else "",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun WeightProgressCard(state: GuideUiState, onLogWeight: () -> Unit) {
    GuideCard {
        Row(verticalAlignment = Alignment.Bottom) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Peso actual", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = state.currentWeight?.let { "${kg(it)} kg" } ?: "Sin registros",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (state.phase == 1) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("Faltan", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${kg(state.remainingKg)} kg",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (state.phase == 1) {
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { state.progress },
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    "Inicio ${kg(NutritionGuide.START_WEIGHT_KG)} kg",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "Meta ${NutritionGuide.TARGET_RANGE}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            PaceBadge(state)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onLogWeight, modifier = Modifier.fillMaxWidth()) {
            Text("Registrar peso de hoy")
        }
    }
}

@Composable
private fun PaceBadge(state: GuideUiState) {
    val loss = state.weeklyLoss
    val (color, text) = when (state.pace) {
        GuideProgress.Pace.NO_DATA ->
            MaterialTheme.colorScheme.onSurfaceVariant to
                "Registra tu peso varios días por semana para ver el ritmo."
        GuideProgress.Pace.IDEAL ->
            MaterialTheme.colorScheme.primary to
                "Ritmo ideal: −${kg(loss!!)} kg/semana. Sigue igual." +
                (state.weeksLeft?.let { " Llegas en ~$it semanas." } ?: "")
        GuideProgress.Pace.TOO_SLOW ->
            GoldRecord to "Lento: −${kg(loss!!)} kg/semana. Si se repite 2 semanas, saca ~150 kcal o suma 2.000 pasos."
        GuideProgress.Pace.TOO_FAST ->
            MaterialTheme.colorScheme.error to
                "Muy rápido: −${kg(loss!!)} kg/semana. Si se repite 2 semanas, suma 150–200 kcal alrededor del entreno."
        GuideProgress.Pace.GAINING ->
            GoldRecord to "Esta semana subiste ${kg(-loss!!)} kg. Revisa porciones y pasos; un día suelto no cuenta."
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(10.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = color)
    }
}

@Composable
private fun ReachedTargetBanner(onStart: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "🎉 ¡Llegaste a tu meta!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                "Ya puedes iniciar la Fase 2: volumen limpio.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onStart,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) { Text("Iniciar Fase 2") }
        }
    }
}

@Composable
private fun LockedPhaseCard(remainingKg: Double) {
    GuideCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Fase 2 bloqueada",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Se habilita cuando registres ${NutritionGuide.TARGET_RANGE}. Te faltan ${kg(remainingKg)} kg. " +
                "Mientras tanto, enfócate en la Fase 1.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GoalCard(phase: NutritionGuide.Phase) {
    GuideCard {
        Text(phase.title, style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(phase.subtitle, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(phase.goal, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun TargetsCard(targets: List<NutritionGuide.Target>) {
    GuideCard {
        Text("Objetivos diarios", style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(6.dp))
        targets.forEachIndexed { index, target ->
            if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            Row(modifier = Modifier.padding(vertical = 7.dp)) {
                Text(
                    target.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    target.value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1.1f)
                )
            }
        }
    }
}

@Composable
private fun MenusCard(menus: List<NutritionGuide.Menu>) {
    var selected by rememberSaveable(menus.size) { mutableIntStateOf(0) }
    val menu = menus.getOrNull(selected) ?: return
    GuideCard {
        Text("Menú del día", style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary)
        if (menus.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                menus.forEachIndexed { index, m ->
                    val isSel = index == selected
                    Text(
                        text = m.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSel) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (isSel) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { selected = index }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        menu.meals.forEach { meal ->
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        meal.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "${meal.kcal} kcal · P ${meal.protein} g",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    meal.foods,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Total: ~${menu.totalKcal} kcal · Proteína ${menu.totalProtein} g · " +
                "Carbos ${menu.totalCarbs} g · Grasas ${menu.totalFat} g",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "Pollo y carne se pesan en crudo; arroz y papa, cocidos; atún y sardinas, escurridos.",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionCard(section: NutritionGuide.Section, initiallyOpen: Boolean) {
    var open by rememberSaveable(section.title) { mutableStateOf(initiallyOpen) }
    val accent = if (section.alert) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (section.alert) Modifier.border(
                    1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f), RoundedCornerShape(16.dp)
                ) else Modifier
            )
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { open = !open }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    "${section.emoji}  ${section.title}",
                    style = MaterialTheme.typography.titleMedium,
                    color = accent,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    if (open) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AnimatedVisibility(visible = open) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 14.dp)) {
                    section.items.forEach { item ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 7.dp, end = 10.dp)
                                    .size(6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (section.alert) MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.primary
                                    )
                            )
                            Text(
                                item,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProtocolNotesCard(notes: String, onEdit: () -> Unit) {
    GuideCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "📋  Mi protocolo",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar")
            }
        }
        Text(
            text = notes.ifBlank {
                "Anota aquí la medicación, dosis y días que te indicó tu médico, y la fecha de tus próximos análisis."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = if (notes.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun GuideCard(content: @Composable () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
    }
}

