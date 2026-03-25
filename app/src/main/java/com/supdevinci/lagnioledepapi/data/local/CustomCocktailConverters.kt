package com.supdevinci.lagnioledepapi.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.supdevinci.lagnioledepapi.model.CustomIngredient

class CustomCocktailConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromIngredients(value: List<CustomIngredient>): String = gson.toJson(value)

    @TypeConverter
    fun toIngredients(value: String): List<CustomIngredient> {
        val type = object : TypeToken<List<CustomIngredient>>() {}.type
        return gson.fromJson(value, type)
    }
}
