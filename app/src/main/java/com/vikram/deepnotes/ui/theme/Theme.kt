package com.vikram.deepnotes.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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

// Create Typography with dynamic text colors based on onBackground
@Composable
fun appTypography(onBackgroundColor: Color) = Typography(
    displayLarge = TextStyle(
        color = onBackgroundColor,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = MaterialTheme.typography.displayLarge.fontSize
    ),
    displayMedium = TextStyle(
        color = onBackgroundColor,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = MaterialTheme.typography.displayMedium.fontSize
    ),
    displaySmall = TextStyle(
        color = onBackgroundColor,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = MaterialTheme.typography.displaySmall.fontSize
    ),
    headlineLarge = TextStyle(
        color = onBackgroundColor,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = MaterialTheme.typography.headlineLarge.fontSize
    ),
    headlineMedium = TextStyle(
        color = onBackgroundColor,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = MaterialTheme.typography.headlineMedium.fontSize
    ),
    headlineSmall = TextStyle(
        color = onBackgroundColor,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = MaterialTheme.typography.headlineSmall.fontSize
    ),
    titleLarge = TextStyle(
        color = onBackgroundColor,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = MaterialTheme.typography.titleLarge.fontSize
    ),
    titleMedium = TextStyle(
        color = onBackgroundColor,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = MaterialTheme.typography.titleMedium.fontSize
    ),
    titleSmall = TextStyle(
        color = onBackgroundColor,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = MaterialTheme.typography.titleSmall.fontSize
    ),
    bodyLarge = TextStyle(
        color = onBackgroundColor,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = MaterialTheme.typography.bodyLarge.fontSize
    ),
    bodyMedium = TextStyle(
        color = onBackgroundColor,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = MaterialTheme.typography.bodyMedium.fontSize
    ),
    bodySmall = TextStyle(
        color = onBackgroundColor,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = MaterialTheme.typography.bodySmall.fontSize
    ),
    labelLarge = TextStyle(
        color = onBackgroundColor,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = MaterialTheme.typography.labelLarge.fontSize
    ),
    labelMedium = TextStyle(
        color = onBackgroundColor,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = MaterialTheme.typography.labelMedium.fontSize
    ),
    labelSmall = TextStyle(
        color = onBackgroundColor,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = MaterialTheme.typography.labelSmall.fontSize
    )
)

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DeepNotesTheme(
    theme: MutableState<String>,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            when (theme.value) {
                Theme.DARK_THEME.toString() -> dynamicDarkColorScheme(context)
                Theme.LIGHT_THEME.toString() -> dynamicLightColorScheme(context)
                Theme.SYSTEM_THEME.toString() -> {
                    if (isSystemInDarkTheme()) dynamicDarkColorScheme(context)
                    else dynamicLightColorScheme(context)
                }
                else -> dynamicLightColorScheme(context)
            }
        }

        theme.value == Theme.DARK_THEME.toString() -> DarkColorScheme
        theme.value == Theme.LIGHT_THEME.toString() -> LightColorScheme
        theme.value == Theme.SYSTEM_THEME.toString() -> {
            if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
        }

        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = appTypography(colorScheme.onBackground),
        content = content
    )
}
