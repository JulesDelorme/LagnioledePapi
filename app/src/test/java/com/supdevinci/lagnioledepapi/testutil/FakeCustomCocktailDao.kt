package com.supdevinci.lagnioledepapi.testutil

import com.supdevinci.lagnioledepapi.data.local.CustomCocktailDao
import com.supdevinci.lagnioledepapi.data.local.CustomCocktailEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCustomCocktailDao(
    initialItems: List<CustomCocktailEntity> = emptyList()
) : CustomCocktailDao {
    private val items = MutableStateFlow(initialItems)
    private var nextId = (initialItems.maxOfOrNull { it.id } ?: 0L) + 1L

    override fun observeAll(): Flow<List<CustomCocktailEntity>> = items

    override fun getById(id: Long): CustomCocktailEntity? =
        items.value.firstOrNull { it.id == id }

    override fun insert(cocktail: CustomCocktailEntity): Long {
        val assignedId = if (cocktail.id == 0L) nextId++ else cocktail.id
        val updated = cocktail.copy(id = assignedId)
        items.value = (items.value.filterNot { it.id == assignedId } + updated)
            .sortedByDescending { it.createdAt }
        return assignedId
    }
}
