package com.supdevinci.lagnioledepapi.view.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GnioleColors = darkColorScheme(
    primary = FoamYellow,
    onPrimary = DarkWood,
    secondary = MutedCream,
    onSecondary = DarkWood,
    tertiary = AperitifOrange,
    onTertiary = DarkWood,
    background = BarrelBrown,
    onBackground = WarmText,
    surface = BottomBarBrown,
    onSurface = WarmText,
    surfaceVariant = TavernCard,
    onSurfaceVariant = MutedCream,
    error = DangerRed,
    onError = WarmText
)

@Composable
fun LaGnioleDePapiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GnioleColors,
        typography = Typography,
        content = content
    )
}
