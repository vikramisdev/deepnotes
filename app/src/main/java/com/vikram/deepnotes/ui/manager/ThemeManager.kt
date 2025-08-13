package com.vikram.deepnotes.ui.manager

import androidx.compose.runtime.mutableStateOf

object ThemeManager {
    // Holds the current theme as a string matching your DeepNotesTheme expected values
    var currentTheme = mutableStateOf("LIGHT") // "LIGHT", "DARK", "SYSTEM"
}
