package com.supdevinci.lagnioledepapi.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.supdevinci.lagnioledepapi.data.AppContainer
import com.supdevinci.lagnioledepapi.model.CocktailSource

object AppViewModelProvider {
    fun cocktailListFactory(appContainer: AppContainer): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                CocktailListViewModel(
                    cocktailRepository = appContainer.cocktailRepository,
                    userStatsRepository = appContainer.userStatsRepository,
                    jokeRepository = appContainer.jokeRepository
                )
            }
        }

    fun cocktailDetailFactory(
        appContainer: AppContainer,
        source: CocktailSource,
        id: String
    ): ViewModelProvider.Factory = viewModelFactory {
        initializer {
                CocktailDetailViewModel(
                    source = source,
                    id = id,
                    cocktailRepository = appContainer.cocktailRepository,
                    customCocktailRepository = appContainer.customCocktailRepository,
                    favoriteCocktailRepository = appContainer.favoriteCocktailRepository,
                    userStatsRepository = appContainer.userStatsRepository,
                    jokeRepository = appContainer.jokeRepository
                )
            }
        }

    fun rankingFactory(appContainer: AppContainer): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                RankingViewModel(
                    rankingRepository = appContainer.rankingRepository,
                    favoriteCocktailRepository = appContainer.favoriteCocktailRepository,
                    customCocktailRepository = appContainer.customCocktailRepository,
                    userStatsRepository = appContainer.userStatsRepository,
                    jokeRepository = appContainer.jokeRepository
                )
            }
        }

    fun cellarFactory(appContainer: AppContainer): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                CellarViewModel(
                    favoriteCocktailRepository = appContainer.favoriteCocktailRepository,
                    customCocktailRepository = appContainer.customCocktailRepository,
                    jokeRepository = appContainer.jokeRepository
                )
            }
        }

    fun bacFactory(appContainer: AppContainer): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                BacTestViewModel(jokeRepository = appContainer.jokeRepository)
            }
        }

    fun createCocktailFactory(appContainer: AppContainer): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                CreateCocktailViewModel(
                    cocktailRepository = appContainer.cocktailRepository,
                    customCocktailRepository = appContainer.customCocktailRepository,
                    jokeRepository = appContainer.jokeRepository
                )
            }
        }
}
