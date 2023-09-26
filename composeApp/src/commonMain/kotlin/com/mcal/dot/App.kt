package com.mcal.dot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mcal.dot.theme.AppTheme
import com.mcal.dot.theme.LocalThemeIsDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun App(
    systemAppearance: (isLight: Boolean) -> Unit = {},
    content: @Composable () -> Unit,
) = AppTheme(systemAppearance) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Diagram Smali Methods")
                },
                actions = {
                    var isDark by LocalThemeIsDark.current
                    IconButton(
                        onClick = { isDark = !isDark }
                    ) {
                        Icon(
                            modifier = Modifier.padding(8.dp).size(20.dp),
                            imageVector = if (isDark) rememberLightMode() else rememberDarkMode(),
                            contentDescription = null
                        )
                    }
                }
            )
        }, content = { paddings ->
            Column(modifier = Modifier.padding(paddings)) {
                content()
            }
        }
    )
}
