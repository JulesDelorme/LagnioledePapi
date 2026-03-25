package com.supdevinci.lagnioledepapi.view.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.supdevinci.lagnioledepapi.view.theme.CreamFoam
import com.supdevinci.lagnioledepapi.view.theme.DarkWood
import com.supdevinci.lagnioledepapi.view.theme.FoamYellow
import com.supdevinci.lagnioledepapi.view.theme.TavernCardDark

@Composable
fun GniolePillButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) FoamYellow else TavernCardDark,
            contentColor = if (selected) DarkWood else CreamFoam
        )
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, color = if (selected) DarkWood else Color.White)
    }
}
