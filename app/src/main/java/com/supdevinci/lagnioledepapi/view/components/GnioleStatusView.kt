package com.supdevinci.lagnioledepapi.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.supdevinci.lagnioledepapi.view.theme.CreamFoam
import com.supdevinci.lagnioledepapi.view.theme.FoamYellow

@Composable
fun GnioleStatusView(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    GnioleCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = FoamYellow
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = CreamFoam,
                textAlign = TextAlign.Center
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = CreamFoam.copy(alpha = 0.78f),
                textAlign = TextAlign.Center
            )
        }
    }
}
