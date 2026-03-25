package com.supdevinci.lagnioledepapi.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.supdevinci.lagnioledepapi.model.Drink
import com.supdevinci.lagnioledepapi.view.components.GnioleCard
import com.supdevinci.lagnioledepapi.view.components.GnioleHeader
import com.supdevinci.lagnioledepapi.view.components.GnioleSearchField
import com.supdevinci.lagnioledepapi.view.components.GnioleStatusView
import com.supdevinci.lagnioledepapi.view.theme.AperitifOrange
import com.supdevinci.lagnioledepapi.view.theme.BarrelBrown
import com.supdevinci.lagnioledepapi.view.theme.CreamFoam
import com.supdevinci.lagnioledepapi.view.theme.DarkWood
import com.supdevinci.lagnioledepapi.view.theme.FoamYellow
import com.supdevinci.lagnioledepapi.view.theme.MutedCream
import com.supdevinci.lagnioledepapi.view.theme.SlateButton
import com.supdevinci.lagnioledepapi.view.theme.TavernCardDark
import com.supdevinci.lagnioledepapi.viewmodel.CocktailListUiState
import com.supdevinci.lagnioledepapi.viewmodel.CocktailListViewModel

@Composable
fun CocktailListScreen(
    viewModel: CocktailListViewModel,
    onOpenDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val openSurprise: (Drink) -> Unit = { cocktail ->
        viewModel.onSurpriseOpened()
        onOpenDetail(cocktail.id)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BarrelBrown, DarkWood))),
        contentPadding = PaddingValues(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GnioleHeader(
                emoji = "🍺",
                title = "Les Cocktails de",
                accentTitle = "Dédé le Barman",
                subtitle = uiState.joke
            )
        }
        item {
            GnioleSearchField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = "Cherche ton breuvage ici...",
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (searchQuery.isBlank()) {
            item {
                SurpriseCocktailCard(
                    cocktail = uiState.surpriseCocktail,
                    isLoading = uiState.isSurpriseLoading,
                    errorMessage = uiState.surpriseErrorMessage,
                    onOpenDetail = openSurprise,
                    onRefresh = viewModel::refreshSurpriseCocktail
                )
            }
        }

        when (val state = uiState) {
            is CocktailListUiState.Loading -> {
                item {
                    GnioleStatusView(
                        title = "On tire la première tournée...",
                        message = "Le comptoir charge les recettes du jour.",
                        isLoading = true
                    )
                }
            }

            is CocktailListUiState.Error -> {
                item {
                    GnioleStatusView(
                        title = "Le bar a renversé le Wi‑Fi",
                        message = state.message
                    )
                }
            }

            is CocktailListUiState.Success -> {
                if (state.remoteCocktails.isEmpty()) {
                    item {
                        GnioleStatusView(
                            title = "Rien sur l'ardoise",
                            message = "Essaie un autre nom ou laisse Dédé fouiller par lui-même."
                        )
                    }
                } else {
                    item { SectionTitle("🍸 Les trouvailles du zinc") }
                    items(state.remoteCocktails.chunked(2)) { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            row.forEach { drink ->
                                RemoteCocktailCard(
                                    drink = drink,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onOpenDetail(drink.id) }
                                )
                            }
                            if (row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SurpriseCocktailCard(
    cocktail: Drink?,
    isLoading: Boolean,
    errorMessage: String?,
    onOpenDetail: (Drink) -> Unit,
    onRefresh: () -> Unit
) {
    GnioleCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
        onClick = {
            if (cocktail != null) {
                onOpenDetail(cocktail)
            }
        }
    ) {
        if (cocktail?.imageUrl != null) {
            AsyncImage(
                model = cocktail.imageUrl,
                contentDescription = cocktail.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(TavernCardDark),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isLoading) "🎲" else "🍹",
                    style = MaterialTheme.typography.displaySmall
                )
            }
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🎲 Surprends-moi Dédé",
                style = MaterialTheme.typography.titleLarge,
                color = FoamYellow,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            when {
                cocktail != null -> {
                    Text(
                        text = cocktail.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = CreamFoam,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = cocktail.shortIngredients(limit = 4),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MutedCream
                    )
                }

                isLoading -> {
                    Text(
                        text = "Le patron secoue le shaker au hasard...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = CreamFoam
                    )
                }

                else -> {
                    Text(
                        text = errorMessage ?: "La surprise du jour n'a pas voulu se montrer.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = CreamFoam
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { cocktail?.let(onOpenDetail) },
                    enabled = cocktail != null,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FoamYellow,
                        contentColor = DarkWood,
                        disabledContainerColor = FoamYellow.copy(alpha = 0.35f),
                        disabledContentColor = DarkWood.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text("Voir la fiche", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onRefresh,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = SlateButton.copy(alpha = 0.35f),
                        contentColor = CreamFoam
                    ),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text("Encore un autre", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = CreamFoam,
        fontWeight = FontWeight.ExtraBold
    )
}

@Composable
private fun RemoteCocktailCard(
    drink: Drink,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GnioleCard(
        modifier = modifier.height(315.dp),
        contentPadding = PaddingValues(0.dp),
        onClick = onClick
    ) {
        AsyncImage(
            model = drink.imageUrl,
            contentDescription = drink.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = TavernCardDark,
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = drink.category ?: "Classique",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        color = FoamYellow,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = drink.alcoholic ?: "Sec",
                    color = MutedCream,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = drink.name,
                style = MaterialTheme.typography.titleLarge,
                color = CreamFoam,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = drink.shortIngredients(limit = 3),
                style = MaterialTheme.typography.bodyMedium,
                color = MutedCream,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
