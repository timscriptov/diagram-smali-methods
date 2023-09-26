package com.mcal.dot.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.io.File

actual object ImageHelper {
    actual fun imageFromFile(path: String): ImageBitmap {
        return org.jetbrains.skia.Image.makeFromEncoded(File(path).readBytes()).toComposeImageBitmap()
    }
}