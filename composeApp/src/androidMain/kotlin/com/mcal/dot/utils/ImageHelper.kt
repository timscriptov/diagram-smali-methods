package com.mcal.dot.utils

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual object ImageHelper {
    actual fun imageFromFile(path: String): ImageBitmap {
        return BitmapFactory.decodeFile(path).asImageBitmap()
    }
}