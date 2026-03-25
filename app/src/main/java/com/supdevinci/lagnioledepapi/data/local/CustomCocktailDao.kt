package com.supdevinci.lagnioledepapi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomCocktailDao {
    @Query("SELECT * FROM custom_cocktails ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<CustomCocktailEntity>>

    @Query("SELECT * FROM custom_cocktails WHERE id = :id LIMIT 1")
    fun getById(id: Long): CustomCocktailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cocktail: CustomCocktailEntity): Long
}
