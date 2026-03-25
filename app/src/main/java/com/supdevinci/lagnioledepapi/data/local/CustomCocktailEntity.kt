package com.supdevinci.lagnioledepapi.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.supdevinci.lagnioledepapi.model.CustomCocktail
import com.supdevinci.lagnioledepapi.model.CustomIngredient

@Entity(tableName = "custom_cocktails")
data class CustomCocktailEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val story: String,
    @ColumnInfo(name = "ingredients_json")
    val ingredientsJson: List<CustomIngredient>,
    val createdAt: Long
)

fun CustomCocktailEntity.toModel(): CustomCocktail = CustomCocktail(
    id = id,
    name = name,
    story = story,
    ingredients = ingredientsJson,
    createdAt = createdAt
)

fun CustomCocktail.toEntity(): CustomCocktailEntity = CustomCocktailEntity(
    id = id,
    name = name,
    story = story,
    ingredientsJson = ingredients,
    createdAt = createdAt
)
