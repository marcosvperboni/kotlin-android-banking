package com.marcosvperboni.bankingapp.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = BankBlue,
    onPrimary = LightSurface,
    primaryContainer = BankBlueLight,
    onPrimaryContainer = LightSurface,
    secondary = BankGold,
    onSecondary = BankBlueDark,
    background = LightBackground,
    surface = LightSurface,
    error = ErrorRed,
)

private val DarkColors = darkColorScheme(
    primary = BankBlueLight,
    onPrimary = DarkBackground,
    primaryContainer = BankBlueDark,
    onPrimaryContainer = LightSurface,
    secondary = BankGoldDark,
    onSecondary = DarkBackground,
    background = DarkBackground,
    surface = DarkSurface,
    error = ErrorRed,
)

@Composable
fun BankingAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = BankingTypography,
        content = content,
    )
}
