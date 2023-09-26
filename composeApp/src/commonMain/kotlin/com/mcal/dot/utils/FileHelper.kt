package com.mcal.dot.utils

import io.ktor.utils.io.core.*
import java.io.*

object FileHelper {
    @JvmStatic
    fun writeToFile(file: File, content: ByteArray) = file.writeBytes(content)

    @JvmStatic
    @Throws(IOException::class)
    fun copyFile(inputStream: InputStream, output: File) {
        inputStream.use { input ->
            FileOutputStream(output).use { output ->
                input.copyTo(output)
            }
        }
    }
}