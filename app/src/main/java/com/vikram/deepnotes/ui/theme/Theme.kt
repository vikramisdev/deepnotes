package com.vikram.deepnotes.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.vikram.deepnotes.Theme

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    onBackground = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    onBackground = Color.Black
)

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DeepNotesTheme(
    theme: MutableState<Theme>,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (theme.value === Theme.DARK_THEME) {
                dynamicDarkColorScheme(context)
            } else if (theme.value === Theme.LIGHT_THEME) {
                dynamicLightColorScheme(context)
            } else if (theme.value === Theme.SYSTEM_THEME) {
                if (isSystemInDarkTheme()) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
                    context
                )
            } else {
                dynamicLightColorScheme(
                    context
                )
            }
        }

        theme.value === Theme.DARK_THEME -> DarkColorScheme
        theme.value === Theme.LIGHT_THEME -> LightColorScheme
        theme.value === Theme.SYSTEM_THEME -> {
            val context = LocalContext.current
            if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
        }
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}