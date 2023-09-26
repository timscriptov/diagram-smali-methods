package com.mcal.dot

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.mcal.dot.utils.FileHelper
import com.mcal.dot.utils.FilePickHelper
import com.mcal.dot.utils.ImageHelper
import java.io.File

class AndroidApp : Application() {
    companion object {
        lateinit var INSTANCE: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}

class AppActivity : ComponentActivity() {
    lateinit var pickLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val systemBarColor = Color.TRANSPARENT
        setContent {
            val view = LocalView.current
            var isLightStatusBars by remember { mutableStateOf(false) }
            if (!view.isInEditMode) {
                LaunchedEffect(isLightStatusBars) {
                    val window = (view.context as Activity).window
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    window.statusBarColor = systemBarColor
                    window.navigationBarColor = systemBarColor
                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightStatusBars = isLightStatusBars
                        isAppearanceLightNavigationBars = isLightStatusBars
                    }
                }
            }
            var smaliPath by remember { mutableStateOf("") }
            App(
                systemAppearance = { isLight -> isLightStatusBars = isLight },
                content = {
                    pickLauncher =
                        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                            if (result.resultCode == -1) {
                                result.data?.data?.let { uri ->
                                    val tmpApkPath = File(AndroidApp.INSTANCE.applicationContext.filesDir, "file.smali")
                                    AndroidApp.INSTANCE.applicationContext.contentResolver.openInputStream(uri)
                                        ?.let { inputStream ->
                                            FileHelper.copyFile(inputStream, tmpApkPath)
                                        }
                                    smaliPath = tmpApkPath.path
                                }
                            }
                        }
                    val imageSource = remember { mutableStateMapOf<String, String>() }
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    pickLauncher.launch(FilePickHelper.pickFile())
                                },
                            value = smaliPath,
                            onValueChange = { newText ->
                                smaliPath = newText.replace("\"", "")
                            },
                            enabled = false,
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
                        Card(
                            modifier = Modifier.fillMaxSize().weight(1f),
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
                                                Image(
                                                    bitmap = ImageHelper.imageFromFile(u),
                                                    contentDescription = ""
                                                )
                                                Divider(modifier = Modifier.padding(bottom = 8.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Text(text = "Copyright 2023 timscriptov")
                    }
                }
            )
        }
    }
}
