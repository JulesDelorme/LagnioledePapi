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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import com.supdevinci.lagnioledepapi.model.CocktailSource
import com.supdevinci.lagnioledepapi.model.CustomCocktail
import com.supdevinci.lagnioledepapi.model.FavoriteCocktailSummary
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
import com.supdevinci.lagnioledepapi.view.theme.TavernCardDark
import com.supdevinci.lagnioledepapi.viewmodel.CellarViewModel

@Composable
fun CellarScreen(
    viewModel: CellarViewModel,
    onOpenDetail: (CocktailSource, String) -> Unit
) {
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
                emoji = "🍾",
                title = "Bienvenue dans",
                accentTitle = "Ma Cave",
                subtitle = uiState.joke
            )
        }
        item {
            GnioleSearchField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = "Cherche dans tes favoris et recettes...",
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            CellarSummaryCard(
                favoritesCount = uiState.favoriteCocktails.size,
                customCount = uiState.customCocktails.size
            )
        }

        when {
            uiState.isCellarEmpty -> {
                item {
                    GnioleStatusView(
                        title = "La cave sonne creux",
                        message = "Ajoute des favoris ou invente une recette maison pour remplir les étagères."
                    )
                }
            }

            uiState.searchQuery.isNotBlank() && !uiState.hasSearchResults -> {
                item {
                    GnioleStatusView(
                        title = "Rien ne colle à ta recherche",
                        message = "Essaie un autre nom, une catégorie ou un ingrédient de tes créations."
                    )
                }
            }

            else -> {
                if (uiState.filteredFavoriteCocktails.isNotEmpty()) {
                    item {
                        CellarSectionTitle("❤️ Les favoris du comptoir")
                    }
                    items(
                        items = uiState.filteredFavoriteCocktails,
                        key = { favorite -> favorite.favoriteKey }
                    ) { favorite ->
                        FavoriteCocktailCard(
                            cocktail = favorite,
                            onClick = { onOpenDetail(favorite.source, favorite.id) }
                        )
                    }
                }

                if (uiState.filteredCustomCocktails.isNotEmpty()) {
                    item {
                        CellarSectionTitle("🏡 Les recettes maison")
                    }
                    items(
                        items = uiState.filteredCustomCocktails,
                        key = { cocktail -> cocktail.id }
                    ) { cocktail ->
                        CustomCocktailCard(
                            cocktail = cocktail,
                            onClick = { onOpenDetail(CocktailSource.LOCAL, cocktail.id.toString()) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CellarSummaryCard(
    favoritesCount: Int,
    customCount: Int
) {
    GnioleCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Le stock du patron",
            style = MaterialTheme.typography.titleLarge,
            color = CreamFoam,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CellarMetricPill(
                modifier = Modifier.weight(1f),
                emoji = "❤️",
                label = "Favoris",
                value = favoritesCount.toString()
            )
            CellarMetricPill(
                modifier = Modifier.weight(1f),
                emoji = "🏡",
                label = "Maisons",
                value = customCount.toString()
            )
        }
    }
}

@Composable
private fun CellarMetricPill(
    emoji: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = TavernCardDark,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$emoji $label",
                color = MutedCream,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = value,
                color = FoamYellow,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun CellarSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = CreamFoam,
        fontWeight = FontWeight.ExtraBold
    )
}

@Composable
private fun FavoriteCocktailCard(
    cocktail: FavoriteCocktailSummary,
    onClick: () -> Unit
) {
    GnioleCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (cocktail.imageUrl != null) {
                AsyncImage(
                    model = cocktail.imageUrl,
                    contentDescription = cocktail.name,
                    modifier = Modifier
                        .size(86.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(TavernCardDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (cocktail.source == CocktailSource.LOCAL) "🏡" else "🍸",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = AperitifOrange,
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Text(
                            text = cocktail.badge,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            color = DarkWood,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Surface(
                        color = TavernCardDark,
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Text(
                            text = if (cocktail.source == CocktailSource.LOCAL) "Maison" else "Favori",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            color = FoamYellow,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = cocktail.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = CreamFoam,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = cocktail.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedCream,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun CustomCocktailCard(
    cocktail: CustomCocktail,
    onClick: () -> Unit
) {
    GnioleCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Text(
            text = "🍾 ${cocktail.name}",
            style = MaterialTheme.typography.titleLarge,
            color = CreamFoam,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = cocktail.ingredients.joinToString(" • ") { ingredient -> "${ingredient.name} ${ingredient.dose}" },
            style = MaterialTheme.typography.bodyMedium,
            color = MutedCream,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (cocktail.story.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = cocktail.story,
                style = MaterialTheme.typography.bodyMedium,
                color = CreamFoam.copy(alpha = 0.82f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
