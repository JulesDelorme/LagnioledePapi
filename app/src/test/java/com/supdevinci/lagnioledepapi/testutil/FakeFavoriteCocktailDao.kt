package com.supdevinci.lagnioledepapi.testutil

import com.supdevinci.lagnioledepapi.data.local.FavoriteCocktailDao
import com.supdevinci.lagnioledepapi.data.local.FavoriteCocktailEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeFavoriteCocktailDao(
    initialItems: List<FavoriteCocktailEntity> = emptyList()
) : FavoriteCocktailDao {
    private val items = MutableStateFlow(initialItems)

    override fun observeAll(): Flow<List<FavoriteCocktailEntity>> =
        items.map { favorites -> favorites.sortedByDescending { it.savedAt } }

    override fun observeFavoriteKeys(): Flow<List<String>> =
        items.map { favorites -> favorites.sortedByDescending { it.savedAt }.map { it.favoriteKey } }

    override fun observeFavoritesCount(): Flow<Int> =
        items.map { favorites -> favorites.size }

    override fun getByKey(key: String): FavoriteCocktailEntity? =
        items.value.firstOrNull { it.favoriteKey == key }

    override fun insert(cocktail: FavoriteCocktailEntity) {
        items.value = items.value.filterNot { it.favoriteKey == cocktail.favoriteKey } + cocktail
    }

    override fun deleteByKey(key: String): Int {
        val before = items.value.size
        items.value = items.value.filterNot { it.favoriteKey == key }
        return before - items.value.size
    }
}
