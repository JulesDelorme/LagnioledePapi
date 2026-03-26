package com.supdevinci.lagnioledepapi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCocktailDao {
    @Query("SELECT * FROM favorite_cocktails ORDER BY saved_at DESC")
    fun observeAll(): Flow<List<FavoriteCocktailEntity>>

    @Query("SELECT favorite_key FROM favorite_cocktails ORDER BY saved_at DESC")
    fun observeFavoriteKeys(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM favorite_cocktails")
    fun observeFavoritesCount(): Flow<Int>

    @Query("SELECT * FROM favorite_cocktails WHERE favorite_key = :key LIMIT 1")
    fun getByKey(key: String): FavoriteCocktailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cocktail: FavoriteCocktailEntity)

    @Query("DELETE FROM favorite_cocktails WHERE favorite_key = :key")
    fun deleteByKey(key: String): Int
}
