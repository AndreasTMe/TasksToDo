package com.andreast.taskstodo.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
//    primary =,
//    onPrimary =,
//    primaryContainer =,
//    onPrimaryContainer =,
//    inversePrimary =,
//    secondary =,
//    onSecondary =,
//    secondaryContainer =,
//    onSecondaryContainer =,
//    tertiary =,
//    onTertiary =,
//    tertiaryContainer =,
//    onTertiaryContainer =,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
//    onSurface =,
//    surfaceVariant =,
//    onSurfaceVariant =,
//    surfaceTint =,
//    inverseSurface =,
//    inverseOnSurface =,
//    error =,
//    onError =,
//    errorContainer =,
//    onErrorContainer =,
    outline = outlineDark,
//    outlineVariant =,
//    scrim =,
//    surfaceBright =,
//    surfaceContainer =,
//    surfaceContainerHigh =,
//    surfaceContainerHighest =,
//    surfaceContainerLow =,
//    surfaceContainerLowest =,
//    surfaceDim =
)

private val LightColorScheme = lightColorScheme(
//    primary =,
//    onPrimary =,
//    primaryContainer =,
//    onPrimaryContainer =,
//    inversePrimary =,
//    secondary =,
//    onSecondary =,
//    secondaryContainer =,
//    onSecondaryContainer =,
//    tertiary =,
//    onTertiary =,
//    tertiaryContainer =,
//    onTertiaryContainer =,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
//    onSurface =,
//    surfaceVariant =,
//    onSurfaceVariant =,
//    surfaceTint =,
//    inverseSurface =,
//    inverseOnSurface =,
//    error =,
//    onError =,
//    errorContainer =,
//    onErrorContainer =,
    outline = outlineLight,
//    outlineVariant =,
//    scrim =,
//    surfaceBright =,
//    surfaceContainer =,
//    surfaceContainerHigh =,
//    surfaceContainerHighest =,
//    surfaceContainerLow =,
//    surfaceContainerLowest =,
//    surfaceDim =
)

@Composable
fun TasksToDoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()

            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

