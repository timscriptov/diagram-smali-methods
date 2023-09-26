package com.mcal.dot

import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.mcal.dot.utils.FileHelper
import com.mcal.dot.utils.NetHelper
import com.mcal.dot.utils.StringHelper.toFileName
import io.ktor.client.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import kotlin.system.measureTimeMillis

class DotRequest(
    private val dot: String,
    private val methodName: String,
    private val outputDir: File,
    private val imageSource: SnapshotStateMap<String, String>
) {
    fun execute() = CoroutineScope(Dispatchers.IO).launch {
        if (!NetHelper.isInternetAvailable()) {
            println("isInternetAvailable")
        } else {
            val client = HttpClient()
            val formData = Parameters.build {
                append("dot_diagram", dot)
            }
            measureTimeMillis {
                async {
                    val response = client.submitForm(
                        url = "https://timscriptov.ru/graphviz/index.php",
                        formParameters = formData,
                        encodeInQuery = false
                    )
                    if (response.status == HttpStatusCode.OK) {
                        val pngFile = File(outputDir, "${methodName.toFileName()}.png")
                        FileHelper.writeToFile(pngFile, response.readBytes())
                        imageSource[methodName] = pngFile.path
//                        println(pngFile.path)
                    }
                }.await()
            }
            client.close()
        }
    }
}