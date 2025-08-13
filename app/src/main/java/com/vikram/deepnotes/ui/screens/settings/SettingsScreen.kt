package com.vikram.deepnotes.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vikram.deepnotes.Theme
import com.vikram.deepnotes.core.GlobalProps

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(props: GlobalProps) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { props.navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            ThemeSegmentedToggle(
                currentTheme = props.currentTheme
            )
        }

        EditorSettings(props)
    }
}


@Composable
fun ThemeSegmentedToggle(
    currentTheme: MutableState<String>,
    modifier: Modifier = Modifier
) {
    val themes = listOf(
        Theme.LIGHT_THEME.toString(),
        Theme.DARK_THEME.toString(),
        Theme.SYSTEM_THEME.toString()
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        themes.forEach { theme ->
            val isSelected = currentTheme.value == theme
            Text(
                text = when(theme) {
                    Theme.LIGHT_THEME.toString() -> "Light"
                    Theme.DARK_THEME.toString() -> "Dark"
                    Theme.SYSTEM_THEME.toString() -> "System"
                    else -> "Light"
                },
                modifier = Modifier
                    .weight(1f)
                    .clickable { currentTheme.value = theme }
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 12.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
