package com.brunoapp.fittrack.presentation.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Renders an image bundled in assets/ (e.g. exercise thumbnails). */
@Composable
fun AssetImage(
    assetPath: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val context = LocalContext.current
    val bitmap by produceState<ImageBitmap?>(initialValue = null, assetPath) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                context.assets.open(assetPath).use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }
    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}

/** Renders an image stored in app-private storage (e.g. progress photos). */
@Composable
fun FileImage(
    path: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val bitmap by produceState<ImageBitmap?>(initialValue = null, path) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                val file = File(path)
                if (!file.exists()) return@runCatching null
                BitmapFactory.decodeFile(path)?.asImageBitmap()
            }.getOrNull()
        }
    }
    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}
