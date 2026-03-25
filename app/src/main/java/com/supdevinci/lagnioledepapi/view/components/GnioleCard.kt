package com.supdevinci.lagnioledepapi.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.supdevinci.lagnioledepapi.view.theme.FoamYellow
import com.supdevinci.lagnioledepapi.view.theme.TavernCard

@Composable
fun GnioleCard(
    modifier: Modifier = Modifier,
    containerColor: Color = TavernCard,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    if (onClick != null) {
        Card(
            modifier = modifier,
            onClick = onClick,
            shape = shape,
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(1.dp, FoamYellow.copy(alpha = 0.28f))
        ) {
            Column(modifier = Modifier.padding(contentPadding), content = content)
        }
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(1.dp, FoamYellow.copy(alpha = 0.28f))
        ) {
            Column(modifier = Modifier.padding(contentPadding), content = content)
        }
    }
}
