package com.supdevinci.lagnioledepapi.view

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.supdevinci.lagnioledepapi.model.CocktailSource
import com.supdevinci.lagnioledepapi.view.components.GnioleCard
import com.supdevinci.lagnioledepapi.view.components.GnioleHeader
import com.supdevinci.lagnioledepapi.view.components.GnioleStatusView
import com.supdevinci.lagnioledepapi.view.theme.AperitifOrange
import com.supdevinci.lagnioledepapi.view.theme.BarrelBrown
import com.supdevinci.lagnioledepapi.view.theme.CreamFoam
import com.supdevinci.lagnioledepapi.view.theme.DarkWood
import com.supdevinci.lagnioledepapi.view.theme.FoamYellow
import com.supdevinci.lagnioledepapi.view.theme.MutedCream
import com.supdevinci.lagnioledepapi.view.theme.SlateButton
import com.supdevinci.lagnioledepapi.view.theme.TavernCardDark
import com.supdevinci.lagnioledepapi.viewmodel.CocktailDetailEvent
import com.supdevinci.lagnioledepapi.viewmodel.CocktailDetailUiState
import com.supdevinci.lagnioledepapi.viewmodel.CocktailDetailViewModel

@Composable
fun CocktailDetailScreen(
    viewModel: CocktailDetailViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is CocktailDetailEvent.ShareCocktail -> {
                    context.startActivity(
                        Intent.createChooser(
                            Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, event.text)
                                type = "text/plain"
                            },
                            "Partager le breuvage"
                        )
                    )
                }

                is CocktailDetailEvent.CopyIngredients -> {
                    clipboardManager.setText(AnnotatedString(event.text))
                }

                is CocktailDetailEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.text)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkWood
    ) { innerPadding ->
        when (val state = uiState) {
            is CocktailDetailUiState.Loading -> DetailStatusScreen(
                modifier = Modifier.padding(innerPadding),
                joke = state.joke,
                onBack = onBack,
                title = "Le barman fouille dans ses fiches...",
                message = "Deux glaçons et ça arrive.",
                isLoading = true
            )

            is CocktailDetailUiState.Error -> DetailStatusScreen(
                modifier = Modifier.padding(innerPadding),
                joke = state.joke,
                onBack = onBack,
                title = "Impossible d'ouvrir la bouteille",
                message = state.message
            )

            is CocktailDetailUiState.Success -> {
                val cocktail = state.cocktail
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(Brush.verticalGradient(listOf(BarrelBrown, DarkWood))),
                    contentPadding = PaddingValues(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = "Retour",
                                    tint = CreamFoam
                                )
                            }
                        }
                    }
                    item {
                        GnioleHeader(
                            emoji = if (cocktail.source == CocktailSource.LOCAL) "🏡" else "🍸",
                            title = "La fiche de",
                            accentTitle = cocktail.name,
                            subtitle = state.joke
                        )
                    }
                    item {
                        GnioleCard {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                DetailActionButton(
                                    text = if (state.isFavorite) "Favori" else "Mettre en favori",
                                    icon = if (state.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                                    modifier = Modifier.weight(1f),
                                    onClick = viewModel::toggleFavorite,
                                    highlighted = state.isFavorite
                                )
                                DetailActionButton(
                                    text = "Partager",
                                    icon = Icons.Outlined.Share,
                                    modifier = Modifier.weight(1f),
                                    onClick = viewModel::shareCocktail
                                )
                                DetailActionButton(
                                    text = "Copier",
                                    icon = Icons.Outlined.ContentCopy,
                                    modifier = Modifier.weight(1f),
                                    onClick = viewModel::copyIngredients
                                )
                            }
                        }
                    }
                    item {
                        if (cocktail.imageUrl != null) {
                            AsyncImage(
                                model = cocktail.imageUrl,
                                contentDescription = cocktail.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                                    .clip(RoundedCornerShape(24.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            GnioleCard(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                androidx.compose.foundation.layout.Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(240.dp)
                                        .background(TavernCardDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🍾", style = MaterialTheme.typography.headlineLarge)
                                }
                            }
                        }
                    }
                    item {
                        GnioleCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = AperitifOrange,
                                    shape = RoundedCornerShape(999.dp)
                                ) {
                                    Text(
                                        text = cocktail.badge,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        color = DarkWood,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = cocktail.category,
                                    color = MutedCream,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = cocktail.accent,
                                style = MaterialTheme.typography.bodyLarge,
                                color = FoamYellow
                            )
                        }
                    }
                    item {
                        GnioleCard {
                            Text(
                                text = "🧂 Ce qu'il y a dedans",
                                style = MaterialTheme.typography.titleLarge,
                                color = CreamFoam
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            cocktail.ingredients.forEach { ingredient ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = ingredient.name,
                                        modifier = Modifier.weight(1f),
                                        color = CreamFoam,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = ingredient.dose.ifBlank { "Au pif" },
                                        color = FoamYellow,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                    item {
                        GnioleCard {
                            Text(
                                text = if (cocktail.source == CocktailSource.LOCAL) "📖 Son histoire" else "📝 Mode d'emploi",
                                style = MaterialTheme.typography.titleLarge,
                                color = CreamFoam
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = cocktail.instructions,
                                color = MutedCream,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier,
    onClick: () -> Unit,
    highlighted: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (highlighted) FoamYellow else SlateButton,
            contentColor = if (highlighted) DarkWood else CreamFoam
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DetailStatusScreen(
    modifier: Modifier = Modifier,
    joke: String,
    onBack: () -> Unit,
    title: String,
    message: String,
    isLoading: Boolean = false
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BarrelBrown, DarkWood))),
        contentPadding = PaddingValues(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Retour",
                    tint = CreamFoam
                )
            }
        }
        item {
            GnioleHeader(
                emoji = "📖",
                title = "Fiche cocktail",
                accentTitle = "En cours d'ouverture",
                subtitle = joke
            )
        }
        item {
            GnioleStatusView(
                title = title,
                message = message,
                isLoading = isLoading
            )
        }
    }
}
