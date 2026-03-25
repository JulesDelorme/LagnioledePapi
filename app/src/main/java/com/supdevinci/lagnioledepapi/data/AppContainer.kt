package com.supdevinci.lagnioledepapi.data

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.supdevinci.lagnioledepapi.data.local.AppDatabase
import com.supdevinci.lagnioledepapi.repository.CocktailRepository
import com.supdevinci.lagnioledepapi.repository.CustomCocktailRepository
import com.supdevinci.lagnioledepapi.repository.FavoriteCocktailRepository
import com.supdevinci.lagnioledepapi.repository.JokeRepository
import com.supdevinci.lagnioledepapi.repository.RankingRepository
import com.supdevinci.lagnioledepapi.repository.UserStatsRepository
import com.supdevinci.lagnioledepapi.service.CocktailService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val cocktailRepository: CocktailRepository
    val customCocktailRepository: CustomCocktailRepository
    val favoriteCocktailRepository: FavoriteCocktailRepository
    val userStatsRepository: UserStatsRepository
    val rankingRepository: RankingRepository
    val jokeRepository: JokeRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val appContext = context.applicationContext
    private val gson = Gson()
    private val retrofit = Retrofit.Builder()
        .baseUrl(CocktailService.BASE_URL)
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    private val database = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java,
        "la_gniole_de_papi.db"
    ).addMigrations(AppDatabase.MIGRATION_1_2).build()

    private val cocktailService = retrofit.create(CocktailService::class.java)

    override val cocktailRepository: CocktailRepository = CocktailRepository(cocktailService)
    override val customCocktailRepository: CustomCocktailRepository =
        CustomCocktailRepository(database.customCocktailDao())
    override val favoriteCocktailRepository: FavoriteCocktailRepository =
        FavoriteCocktailRepository(database.favoriteCocktailDao())
    override val userStatsRepository: UserStatsRepository =
        UserStatsRepository(database.userStatsDao())
    override val rankingRepository: RankingRepository = RankingRepository()
    override val jokeRepository: JokeRepository = JokeRepository()
}
