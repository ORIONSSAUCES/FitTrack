package com.brunoapp.fittrack.presentation.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.brunoapp.fittrack.R
import com.brunoapp.fittrack.domain.model.ProgressPhoto
import com.brunoapp.fittrack.presentation.components.FileImage

/** Side-by-side comparison of two progress photo entries. */
@Composable
fun PhotoCompareDialog(
    photos: List<ProgressPhoto>,   // newest first
    onDismiss: () -> Unit
) {
    if (photos.size < 2) return

    // Defaults: oldest on the left, newest on the right
    var leftId by remember { mutableStateOf(photos.last().id) }
    var rightId by remember { mutableStateOf(photos.first().id) }
    var pose by remember { mutableIntStateOf(0) }  // 0 front, 1 side, 2 back

    val left = photos.firstOrNull { it.id == leftId } ?: photos.last()
    val right = photos.firstOrNull { it.id == rightId } ?: photos.first()

    fun pathFor(photo: ProgressPhoto): String? = when (pose) {
        0 -> photo.frontPhotoPath
        1 -> photo.sidePhotoPath
        else -> photo.backPhotoPath
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.photo_compare_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    listOf(
                        stringResource(R.string.photo_front),
                        stringResource(R.string.photo_side),
                        stringResource(R.string.photo_back)
                    ).forEachIndexed { index, label ->
                        SegmentedButton(
                            selected = pose == index,
                            onClick = { pose = index },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = 3)
                        ) { Text(label) }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ComparePane(
                        photos = photos,
                        selected = left,
                        imagePath = pathFor(left),
                        onSelect = { leftId = it },
                        modifier = Modifier.weight(1f)
                    )
                    ComparePane(
                        photos = photos,
                        selected = right,
                        imagePath = pathFor(right),
                        onSelect = { rightId = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Weight difference
                if (left.weightKg != null && right.weightKg != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    val diff = right.weightKg!! - left.weightKg!!
                    Text(
                        text = stringResource(
                            R.string.photo_compare_diff,
                            (if (diff >= 0) "+" else "") + "%.1f".format(diff)
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun ComparePane(
    photos: List<ProgressPhoto>,
    selected: ProgressPhoto,
    imagePath: String?,
    onSelect: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        var menuExpanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(
                onClick = { menuExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selected.date, style = MaterialTheme.typography.labelMedium)
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                photos.forEach { photo ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                photo.date +
                                    (photo.weightKg?.let { "  ·  %.1f kg".format(it) } ?: "")
                            )
                        },
                        onClick = {
                            onSelect(photo.id)
                            menuExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (imagePath != null) {
            FileImage(
                path = imagePath,
                contentDescription = selected.date,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = stringResource(R.string.photo_compare_missing),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = selected.weightKg?.let { "%.1f kg".format(it) } ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
