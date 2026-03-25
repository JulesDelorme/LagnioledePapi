package com.supdevinci.lagnioledepapi.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.supdevinci.lagnioledepapi.view.theme.CreamFoam
import com.supdevinci.lagnioledepapi.view.theme.FoamYellow
import com.supdevinci.lagnioledepapi.view.theme.MutedCream

@Composable
fun GnioleHeader(
    emoji: String,
    title: String,
    accentTitle: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "$emoji $title",
            style = MaterialTheme.typography.headlineMedium,
            color = CreamFoam
        )
        Text(
            text = accentTitle,
            style = MaterialTheme.typography.headlineLarge,
            color = FoamYellow,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "\"$subtitle\"",
            style = MaterialTheme.typography.bodyLarge,
            color = MutedCream,
            fontStyle = FontStyle.Italic
        )
    }
}
