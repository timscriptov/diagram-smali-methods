package com.mcal.dot.utils

import androidx.compose.ui.graphics.ImageBitmap

expect object ImageHelper {
    fun imageFromFile(path: String): ImageBitmap
}