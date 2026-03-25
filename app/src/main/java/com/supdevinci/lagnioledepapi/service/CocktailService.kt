package com.supdevinci.lagnioledepapi.service

import com.supdevinci.lagnioledepapi.model.CocktailResponse
import com.supdevinci.lagnioledepapi.model.IngredientCatalogResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailService {
    @GET("search.php")
    suspend fun searchCocktails(@Query("s") query: String): CocktailResponse

    @GET("search.php")
    suspend fun listCocktailsByFirstLetter(@Query("f") letter: String): CocktailResponse

    @GET("lookup.php")
    suspend fun getCocktailById(@Query("i") id: String): CocktailResponse

    @GET("random.php")
    suspend fun getRandomCocktail(): CocktailResponse

    @GET("list.php")
    suspend fun listIngredients(@Query("i") filter: String = "list"): IngredientCatalogResponse

    companion object {
        const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"
    }
}
