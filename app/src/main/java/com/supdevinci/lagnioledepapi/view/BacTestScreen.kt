package com.supdevinci.lagnioledepapi.view

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supdevinci.lagnioledepapi.model.BacDrinkSelection
import com.supdevinci.lagnioledepapi.model.BacEstimate
import com.supdevinci.lagnioledepapi.model.BacLevel
import com.supdevinci.lagnioledepapi.view.components.GnioleCard
import com.supdevinci.lagnioledepapi.view.components.GnioleHeader
import com.supdevinci.lagnioledepapi.view.components.GniolePillButton
import com.supdevinci.lagnioledepapi.view.theme.BarrelBrown
import com.supdevinci.lagnioledepapi.view.theme.CreamFoam
import com.supdevinci.lagnioledepapi.view.theme.DarkWood
import com.supdevinci.lagnioledepapi.view.theme.DangerRed
import com.supdevinci.lagnioledepapi.view.theme.FoamYellow
import com.supdevinci.lagnioledepapi.view.theme.MutedCream
import com.supdevinci.lagnioledepapi.view.theme.SuccessGreen
import com.supdevinci.lagnioledepapi.view.theme.TavernCard
import com.supdevinci.lagnioledepapi.view.theme.TavernCardDark
import com.supdevinci.lagnioledepapi.viewmodel.BacTestViewModel
import com.supdevinci.lagnioledepapi.viewmodel.BacUiState
import java.util.Locale
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

@Composable
fun BacTestScreen(viewModel: BacTestViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BarrelBrown, DarkWood))),
        contentPadding = PaddingValues(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GnioleHeader(
                emoji = "🧪",
                title = "Test du",
                accentTitle = "Taux d'Ivresse",
                subtitle = uiState.joke
            )
        }
        item {
            GnioleCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Ton poids (kg)",
                    style = MaterialTheme.typography.titleMedium,
                    color = CreamFoam
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.weightInput,
                    onValueChange = viewModel::onWeightChange,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CreamFoam,
                        unfocusedContainerColor = CreamFoam,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = DarkWood,
                        unfocusedTextColor = DarkWood,
                        cursorColor = DarkWood
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "T'es un gars ou une gonzesse ?",
                    style = MaterialTheme.typography.titleMedium,
                    color = CreamFoam
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GniolePillButton(
                        text = "🧔 Un gars",
                        selected = uiState.isMale,
                        onClick = { viewModel.setGender(true) },
                        modifier = Modifier.weight(1f)
                    )
                    GniolePillButton(
                        text = "👩 Une gonzesse",
                        selected = !uiState.isMale,
                        onClick = { viewModel.setGender(false) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🍻 Balance ce que t'as descendu",
                    style = MaterialTheme.typography.titleLarge,
                    color = CreamFoam
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = viewModel::resetDrinks) {
                    Text("Reset", color = FoamYellow, fontWeight = FontWeight.Bold)
                }
            }
        }
        items(uiState.drinks.chunked(2)) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { selection ->
                    DrinkPresetCard(
                        selection = selection,
                        modifier = Modifier.weight(1f),
                        onAdd = { viewModel.incrementDrink(selection.preset.id) },
                        onRemove = { viewModel.decrementDrink(selection.preset.id) }
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        item {
            BacResultCard(
                uiState = uiState,
                onSimulate = viewModel::openSimulation
            )
        }
    }

    if (uiState.isSimulationVisible) {
        BreathSimulationDialog(
            isDangerous = uiState.result.isDangerous,
            onDismiss = viewModel::dismissSimulation
        )
    }
}

@Composable
private fun DrinkPresetCard(
    selection: BacDrinkSelection,
    modifier: Modifier,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    GnioleCard(
        modifier = modifier,
        containerColor = TavernCard
    ) {
        Text(
            text = "${selection.preset.emoji} ${selection.preset.name}",
            style = MaterialTheme.typography.titleMedium,
            color = CreamFoam
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${selection.preset.volumeMl}ml • ${selection.preset.alcoholPercent.toInt()}%",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedCream
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Remove, contentDescription = null, tint = FoamYellow)
            }
            Text(
                text = selection.quantity.toString(),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = CreamFoam,
                fontWeight = FontWeight.ExtraBold
            )
            IconButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = null, tint = FoamYellow)
            }
        }
    }
}

@Composable
private fun BacResultCard(
    uiState: BacUiState,
    onSimulate: () -> Unit
) {
    val shakeOffset = if (uiState.result.isDangerous) {
        val transition = rememberInfiniteTransition(label = "dangerShake")
        transition.animateFloat(
            initialValue = -4f,
            targetValue = 4f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 70),
                repeatMode = RepeatMode.Reverse
            ),
            label = "shakeOffset"
        )
    } else {
        rememberUpdatedState(0f)
    }

    GnioleCard(
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = shakeOffset.value.dp)
    ) {
        Text(
            text = "💨 Verdict de l'alco-téléphone",
            style = MaterialTheme.typography.titleLarge,
            color = CreamFoam
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = String.format(Locale.FRANCE, "%.2f g/L", uiState.result.rateGPerL),
            style = MaterialTheme.typography.headlineLarge,
            color = when (uiState.result.level) {
                BacLevel.ZERO -> MutedCream
                BacLevel.LOW -> SuccessGreen
                BacLevel.MEDIUM -> FoamYellow
                BacLevel.HIGH -> DangerRed
            },
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = uiState.result.message,
            style = MaterialTheme.typography.bodyLarge,
            color = CreamFoam
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Alcool pur cumulé: ${String.format(Locale.FRANCE, "%.1f", uiState.result.totalPureAlcoholMl)} mL",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedCream
        )
        if (uiState.result.to050 != null || uiState.result.to020 != null) {
            Spacer(modifier = Modifier.height(16.dp))
            uiState.result.to050?.let { estimate ->
                EstimateRow(label = "Sous 0,5 g/L", estimate = estimate)
            }
            uiState.result.to020?.let { estimate ->
                EstimateRow(label = "Sous 0,2 g/L", estimate = estimate)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Estimation pédagogique uniquement. Ce n'est pas un feu vert pour prendre le volant.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedCream
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onSimulate,
            enabled = uiState.result.totalPureAlcoholMl > 0.0,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (uiState.result.isDangerous) DangerRed else FoamYellow,
                contentColor = DarkWood
            ),
            shape = RoundedCornerShape(999.dp)
        ) {
            Text(
                text = if (uiState.result.isDangerous) "💥 Souffle, si t'oses" else "🎺 Faux test de souffle",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EstimateRow(
    label: String,
    estimate: BacEstimate
) {
    Text(
        text = "$label: vers ${estimate.estimatedTimeLabel()} (${estimate.durationLabel()})",
        style = MaterialTheme.typography.bodyMedium,
        color = CreamFoam
    )
    Spacer(modifier = Modifier.height(4.dp))
}

private fun BacEstimate.estimatedTimeLabel(): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return Instant.ofEpochMilli(estimatedAt)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

private fun BacEstimate.durationLabel(): String {
    val hours = minutesUntilThreshold / 60
    val minutes = minutesUntilThreshold % 60
    return when {
        hours > 0 && minutes > 0 -> "dans ${hours}h${minutes.toString().padStart(2, '0')}"
        hours > 0 -> "dans ${hours}h"
        else -> "dans ${minutes} min"
    }
}

@Composable
private fun BreathSimulationDialog(
    isDangerous: Boolean,
    onDismiss: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "breathProgress")

    LaunchedEffect(Unit) {
        delay(200L)
        progress = 1f
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TavernCardDark,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (isDangerous) "Je rentre à pied" else "Remets-en une",
                    color = FoamYellow,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        title = {
            Text(
                text = if (isDangerous) "💨 Souffle pas trop fort non plus" else "💨 Souffle dans le téléphone",
                color = CreamFoam
            )
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(92.dp),
                        color = if (isDangerous) DangerRed else FoamYellow,
                        trackColor = CreamFoam.copy(alpha = 0.18f),
                        strokeWidth = 8.dp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = if (isDangerous) {
                            "Verdict: le téléphone te conseille un canapé, pas un volant. 🛋️"
                        } else {
                            "Verdict: t'as encore les yeux en face des trous. 👀"
                        },
                        color = CreamFoam,
                        textAlign = TextAlign.Center
                    )
                    if (isDangerous) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Mode beauf activé: vision trouble et voix de karaoké.",
                            color = DangerRed,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    )
}
