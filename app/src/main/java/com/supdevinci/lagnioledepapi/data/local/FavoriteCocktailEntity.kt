package com.supdevinci.lagnioledepapi.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.supdevinci.lagnioledepapi.model.CocktailDetail
import com.supdevinci.lagnioledepapi.model.CocktailSource
import com.supdevinci.lagnioledepapi.model.FavoriteCocktailSummary
import com.supdevinci.lagnioledepapi.model.favoriteKey

@Entity(tableName = "favorite_cocktails")
data class FavoriteCocktailEntity(
    @PrimaryKey
    @ColumnInfo(name = "favorite_key")
    val favoriteKey: String,
    val source: String,
    @ColumnInfo(name = "remote_id_or_local_id")
    val remoteIdOrLocalId: String,
    val name: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    val category: String,
    val badge: String,
    @ColumnInfo(name = "saved_at")
    val savedAt: Long
)

fun FavoriteCocktailEntity.toModel(): FavoriteCocktailSummary = FavoriteCocktailSummary(
    favoriteKey = favoriteKey,
    source = when (source) {
        "local" -> CocktailSource.LOCAL
        else -> CocktailSource.REMOTE
    },
    id = remoteIdOrLocalId,
    name = name,
    imageUrl = imageUrl,
    category = category,
    badge = badge,
    savedAt = savedAt
)

fun CocktailDetail.toFavoriteEntity(
    savedAt: Long = System.currentTimeMillis()
): FavoriteCocktailEntity = FavoriteCocktailEntity(
    favoriteKey = favoriteKey(),
    source = source.name.lowercase(),
    remoteIdOrLocalId = id,
    name = name,
    imageUrl = imageUrl,
    category = category,
    badge = badge,
    savedAt = savedAt
)
