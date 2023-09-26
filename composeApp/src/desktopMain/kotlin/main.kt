import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.mcal.dot.App
import com.mcal.dot.DrawFlowDiagram
import com.mcal.dot.utils.ImageHelper.imageFromFile
import java.awt.Dimension
import java.io.File

fun main() = application {
    Window(
        title = "Diagram Smali Methods",
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        onCloseRequest = ::exitApplication,
    ) {
        window.minimumSize = Dimension(350, 600)
        val imageSource = remember { mutableStateMapOf<String, String>() }
        App(
            content = {
                Row(modifier = Modifier.padding(16.dp)) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        var smaliPath by remember { mutableStateOf("") }
                        OutlinedTextField(
                            modifier = Modifier
                                .widthIn(max = 300.dp)
                                .fillMaxWidth(),
                            value = smaliPath,
                            onValueChange = { newText ->
                                smaliPath = newText.replace("\"", "")
                            },
                            singleLine = true,
                            label = { Text("Enter smali path...") },
                            shape = RoundedCornerShape(16.dp),
                        )
                        Button(
                            modifier = Modifier.padding(8.dp),
                            onClick = {
                                if (smaliPath.isNotEmpty()) {
                                    val tmpSmaliFile = File(smaliPath)
                                    if (tmpSmaliFile.exists()) {
                                        DrawFlowDiagram(tmpSmaliFile, null, tmpSmaliFile.parentFile, imageSource).run()
                                    }
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Text(text = "Graph")
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "Copyright 2023 timscriptov")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        LazyColumn {
                            if (imageSource.isNotEmpty()) {
                                imageSource.forEach { (t, u) ->
                                    item {
                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(modifier = Modifier.padding(8.dp), text = t)
                                            println(u)
                                            Image(
                                                bitmap = imageFromFile(u),
                                                contentDescription = ""
                                            )
                                            Divider(modifier = Modifier.padding(bottom = 8.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}
