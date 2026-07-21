package com.brunoapp.fittrack.data.backup

import android.content.Context
import android.net.Uri
import com.brunoapp.fittrack.data.database.dao.BackupDao
import com.brunoapp.fittrack.core.utils.CsvBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CsvExporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val backupDao: BackupDao
) {
    suspend fun exportWeights(uri: Uri): BackupResult = withContext(Dispatchers.IO) {
        runCatching {
            val rows = backupDao.allWeights()
                .sortedWith(compareBy({ it.date }, { it.time }))
                .map { listOf(it.date, it.time, it.weightKg.toString(), it.notes) }
            val csv = CsvBuilder.build(
                header = listOf("fecha", "hora", "peso_kg", "notas"),
                rows = rows
            )
            write(uri, csv)
            BackupResult.Success
        }.getOrElse { BackupResult.Error(it.message ?: "Error") }
    }

    suspend fun exportWorkouts(uri: Uri): BackupResult = withContext(Dispatchers.IO) {
        runCatching {
            val sessions = backupDao.allSessions().filter { !it.isActive }
                .sortedBy { it.startTime }
            val rows = sessions.map {
                listOf(it.startTime, it.name, it.endTime.orEmpty(),
                    it.totalVolumeKg.toString(), it.notes)
            }
            val csv = CsvBuilder.build(
                header = listOf("inicio", "rutina", "fin", "volumen_kg", "notas"),
                rows = rows
            )
            write(uri, csv)
            BackupResult.Success
        }.getOrElse { BackupResult.Error(it.message ?: "Error") }
    }

    private fun write(uri: Uri, content: String) {
        context.contentResolver.openOutputStream(uri, "wt")?.use {
            it.write(content.toByteArray())
        } ?: error("No se pudo abrir el archivo")
    }
}
