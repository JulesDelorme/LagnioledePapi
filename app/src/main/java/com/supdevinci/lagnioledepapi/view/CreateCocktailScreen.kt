package com.supdevinci.lagnioledepapi.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supdevinci.lagnioledepapi.model.CustomCocktail
import com.supdevinci.lagnioledepapi.view.components.GnioleCard
import com.supdevinci.lagnioledepapi.view.components.GnioleHeader
import com.supdevinci.lagnioledepapi.view.theme.BarrelBrown
import com.supdevinci.lagnioledepapi.view.theme.CreamFoam
import com.supdevinci.lagnioledepapi.view.theme.DarkWood
import com.supdevinci.lagnioledepapi.view.theme.FoamYellow
import com.supdevinci.lagnioledepapi.view.theme.MutedCream
import com.supdevinci.lagnioledepapi.view.theme.SlateButton
import com.supdevinci.lagnioledepapi.view.theme.TavernCardDark
import com.supdevinci.lagnioledepapi.viewmodel.CreateCocktailViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateCocktailScreen(
    viewModel: CreateCocktailViewModel,
    onOpenDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessages.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BarrelBrown, DarkWood)))
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GnioleHeader(
                emoji = "🍹",
                title = "Invente ton",
                accentTitle = "Breuvage Perso",
                subtitle = uiState.joke
            )

            FormLabel("Balance le nom de ton cocktail")
            CreamTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                placeholder = "Ex: La Cuite de Dédé"
            )

            FormLabel("Les ingrédients (ouais, faut quand même savoir)")
            when {
                uiState.isLoadingIngredientCatalog -> {
                    Text(
                        text = "On récupère la liste officielle des ingrédients chez TheCocktailDB...",
                        color = MutedCream,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                uiState.ingredientCatalogError != null -> {
                    Text(
                        text = "${uiState.ingredientCatalogError} Tu peux quand même taper ton ingrédient à la main.",
                        color = MutedCream,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                else -> {
                    Text(
                        text = "Suggestions servies par TheCocktailDB. Tape quelques lettres pour affiner.",
                        color = MutedCream,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.suggestedIngredients.forEach { ingredient ->
                    FilterChip(
                        selected = uiState.ingredientInput == ingredient,
                        onClick = { viewModel.selectSuggestedIngredient(ingredient) },
                        label = { Text(ingredient) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = TavernCardDark,
                            labelColor = CreamFoam,
                            selectedContainerColor = FoamYellow,
                            selectedLabelColor = DarkWood
                        )
                    )
                }
            }
            if (
                !uiState.isLoadingIngredientCatalog &&
                uiState.ingredientCatalogError == null &&
                uiState.ingredientInput.isNotBlank() &&
                uiState.suggestedIngredients.isEmpty()
            ) {
                Text(
                    text = "Aucune suggestion API pour ce nom-là. Tu peux quand même l'ajouter à la main.",
                    color = MutedCream,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CreamTextField(
                    value = uiState.ingredientInput,
                    onValueChange = viewModel::updateIngredientInput,
                    placeholder = "Ingrédient",
                    modifier = Modifier.weight(1.4f)
                )
                CreamTextField(
                    value = uiState.doseInput,
                    onValueChange = viewModel::updateDoseInput,
                    placeholder = "Dose",
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Text
                )
                IconButton(
                    onClick = viewModel::addIngredient,
                    modifier = Modifier
                        .background(FoamYellow, RoundedCornerShape(16.dp))
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ajouter",
                        tint = DarkWood
                    )
                }
            }

            if (uiState.ingredients.isNotEmpty()) {
                GnioleCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "🧂 Ce qu'il y a dans la marmite",
                        style = MaterialTheme.typography.titleMedium,
                        color = CreamFoam
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    uiState.ingredients.forEachIndexed { index, ingredient ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ingredient.name,
                                    color = CreamFoam,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = ingredient.dose,
                                    color = MutedCream,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            IconButton(onClick = { viewModel.removeIngredient(index) }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Supprimer",
                                    tint = FoamYellow
                                )
                            }
                        }
                    }
                }
            }

            FormLabel("Raconte ton histoire (si t'as envie)")
            CreamTextField(
                value = uiState.story,
                onValueChange = viewModel::updateStory,
                placeholder = "Genre comment tu l'as inventé en rentrant du PMU...",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            )

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage.orEmpty(),
                    color = FoamYellow,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = viewModel::saveCocktail,
                enabled = uiState.canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SlateButton,
                    contentColor = CreamFoam,
                    disabledContainerColor = SlateButton.copy(alpha = 0.6f),
                    disabledContentColor = CreamFoam.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = if (uiState.isSaving) "On met ça en bouteille..." else "✨ Valider le breuvage ! 🍻",
                    fontWeight = FontWeight.ExtraBold
                )
            }

            if (uiState.savedCocktails.isNotEmpty()) {
                FormLabel("🏡 Tes recettes maison")
                Text(
                    text = "Elles restent ici au frais pendant que l'onglet Cocktails reste branché 100% TheCocktailDB.",
                    color = MutedCream,
                    style = MaterialTheme.typography.bodyMedium
                )
                uiState.savedCocktails.forEach { cocktail ->
                    SavedCocktailCard(
                        cocktail = cocktail,
                        onClick = { onOpenDetail(cocktail.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = CreamFoam,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun CreamTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = { Text(placeholder, color = DarkWood.copy(alpha = 0.4f)) },
        shape = RoundedCornerShape(18.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
}

@Composable
private fun SavedCocktailCard(
    cocktail: CustomCocktail,
    onClick: () -> Unit
) {
    GnioleCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Text(
            text = "🍾 ${cocktail.name}",
            style = MaterialTheme.typography.titleMedium,
            color = CreamFoam,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = cocktail.ingredients.joinToString(" • ") { "${it.name} ${it.dose}" },
            style = MaterialTheme.typography.bodyMedium,
            color = MutedCream,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (cocktail.story.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = cocktail.story,
                style = MaterialTheme.typography.bodyMedium,
                color = CreamFoam.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
