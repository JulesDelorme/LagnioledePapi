package com.supdevinci.lagnioledepapi.repository

import com.supdevinci.lagnioledepapi.data.local.FavoriteCocktailDao
import com.supdevinci.lagnioledepapi.data.local.toFavoriteEntity
import com.supdevinci.lagnioledepapi.data.local.toModel
import com.supdevinci.lagnioledepapi.model.CocktailDetail
import com.supdevinci.lagnioledepapi.model.FavoriteCocktailSummary
import com.supdevinci.lagnioledepapi.model.favoriteKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoriteCocktailRepository(
    private val dao: FavoriteCocktailDao
) {
    fun observeFavoriteCocktails(): Flow<List<FavoriteCocktailSummary>> =
        dao.observeAll().map { favorites -> favorites.map { it.toModel() } }

    fun observeFavoriteKeys(): Flow<Set<String>> =
        dao.observeFavoriteKeys().map { keys -> keys.toSet() }

    fun observeFavoritesCount(): Flow<Int> = dao.observeFavoritesCount()

    suspend fun isFavorite(key: String): Boolean = withContext(Dispatchers.IO) {
        dao.getByKey(key) != null
    }

    suspend fun toggleFavorite(detail: CocktailDetail): Boolean {
        return withContext(Dispatchers.IO) {
            val key = detail.favoriteKey()
            if (dao.getByKey(key) != null) {
                dao.deleteByKey(key)
                false
            } else {
                dao.insert(detail.toFavoriteEntity())
                true
            }
        }
    }
}
