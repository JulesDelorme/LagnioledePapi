package com.supdevinci.lagnioledepapi.repository

import com.supdevinci.lagnioledepapi.data.local.CustomCocktailDao
import com.supdevinci.lagnioledepapi.data.local.toEntity
import com.supdevinci.lagnioledepapi.data.local.toModel
import com.supdevinci.lagnioledepapi.model.CustomCocktail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CustomCocktailRepository(
    private val dao: CustomCocktailDao
) {
    fun observeCustomCocktails(): Flow<List<CustomCocktail>> =
        dao.observeAll().map { entities -> entities.map { it.toModel() } }

    fun observeCount(): Flow<Int> = observeCustomCocktails().map { cocktails -> cocktails.size }

    suspend fun getCustomCocktail(id: Long): CustomCocktail? = withContext(Dispatchers.IO) {
        dao.getById(id)?.toModel()
    }

    suspend fun saveCustomCocktail(cocktail: CustomCocktail) {
        withContext(Dispatchers.IO) {
            dao.insert(cocktail.toEntity())
        }
    }
}
