package com.supdevinci.lagnioledepapi.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supdevinci.lagnioledepapi.model.RankingEntry
import com.supdevinci.lagnioledepapi.model.RankingRegion
import com.supdevinci.lagnioledepapi.view.components.GnioleCard
import com.supdevinci.lagnioledepapi.view.components.GnioleHeader
import com.supdevinci.lagnioledepapi.view.components.GniolePillButton
import com.supdevinci.lagnioledepapi.view.theme.AperitifOrange
import com.supdevinci.lagnioledepapi.view.theme.BarrelBrown
import com.supdevinci.lagnioledepapi.view.theme.CreamFoam
import com.supdevinci.lagnioledepapi.view.theme.DarkWood
import com.supdevinci.lagnioledepapi.view.theme.FoamYellow
import com.supdevinci.lagnioledepapi.view.theme.MutedCream
import com.supdevinci.lagnioledepapi.view.theme.TavernCardDark
import com.supdevinci.lagnioledepapi.viewmodel.RankingViewModel

@Composable
fun RankingScreen(viewModel: RankingViewModel) {
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
                emoji = "🏆",
                title = "Le Palmarès des",
                accentTitle = "Gros Soiffards",
                subtitle = uiState.joke
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RankingRegion.entries.forEach { region ->
                    GniolePillButton(
                        text = when (region) {
                            RankingRegion.MONDIAL -> "🌍 Mondial"
                            RankingRegion.EUROPEEN -> "🇪🇺 Européen"
                            RankingRegion.FRANCAIS -> "🇫🇷 Français"
                        },
                        selected = uiState.selectedRegion == region,
                        onClick = { viewModel.onRegionSelected(region) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        itemsIndexed(uiState.rankings) { index, entry ->
            RankingCard(rank = index + 1, entry = entry)
        }
    }
}

@Composable
private fun RankingCard(
    rank: Int,
    entry: RankingEntry
) {
    GnioleCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = if (entry.isLocalProfile) TavernCardDark else com.supdevinci.lagnioledepapi.view.theme.TavernCard
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = when (rank) {
                    1 -> "🏆"
                    2 -> "🥈"
                    3 -> "🥉"
                    else -> rank.toString()
                },
                style = MaterialTheme.typography.headlineMedium,
                color = FoamYellow
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${entry.flag} ${entry.name}",
                    style = MaterialTheme.typography.titleLarge,
                    color = CreamFoam,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = when (entry.badge) {
                        "Foie de Champion" -> AperitifOrange
                        "Sacré Poivrot" -> TavernCardDark
                        "Profil local" -> FoamYellow
                        else -> FoamYellow
                    },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = entry.badge,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        color = DarkWood,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (entry.statsSummary != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = entry.statsSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedCream
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = entry.score.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = FoamYellow,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = entry.scoreLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedCream
                )
            }
        }
    }
}
