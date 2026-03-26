package com.supdevinci.lagnioledepapi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.LocalBar
import androidx.compose.material.icons.outlined.Science
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.supdevinci.lagnioledepapi.model.CocktailSource
import com.supdevinci.lagnioledepapi.view.BacTestScreen
import com.supdevinci.lagnioledepapi.view.CellarScreen
import com.supdevinci.lagnioledepapi.view.CocktailDetailScreen
import com.supdevinci.lagnioledepapi.view.CocktailListScreen
import com.supdevinci.lagnioledepapi.view.CreateCocktailScreen
import com.supdevinci.lagnioledepapi.view.RankingScreen
import com.supdevinci.lagnioledepapi.viewmodel.AppViewModelProvider
import com.supdevinci.lagnioledepapi.viewmodel.BacTestViewModel
import com.supdevinci.lagnioledepapi.viewmodel.CellarViewModel
import com.supdevinci.lagnioledepapi.viewmodel.CocktailDetailViewModel
import com.supdevinci.lagnioledepapi.viewmodel.CocktailListViewModel
import com.supdevinci.lagnioledepapi.viewmodel.CreateCocktailViewModel
import com.supdevinci.lagnioledepapi.viewmodel.RankingViewModel

data class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

object Routes {
    const val cocktails = "cocktails"
    const val cellar = "cellar"
    const val ranking = "ranking"
    const val bacTest = "bac_test"
    const val createCocktail = "create_cocktail"
    const val cocktailDetail = "cocktail_detail/{source}/{id}"

    fun cocktailDetail(source: CocktailSource, id: String): String =
        "cocktail_detail/${source.name.lowercase()}/$id"

    fun sourceFromRouteArgument(source: String?): CocktailSource =
        when (source) {
            "local" -> CocktailSource.LOCAL
            else -> CocktailSource.REMOTE
        }
}

val topLevelDestinations = listOf(
    TopLevelDestination(Routes.cocktails, "Cocktails", Icons.Outlined.LocalBar),
    TopLevelDestination(Routes.cellar, "Ma cave", Icons.Outlined.Favorite),
    TopLevelDestination(Routes.ranking, "Classement", Icons.Outlined.EmojiEvents),
    TopLevelDestination(Routes.bacTest, "Test", Icons.Outlined.Science),
    TopLevelDestination(Routes.createCocktail, "Créer", Icons.Outlined.Add)
)

@Composable
fun CocktailNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val appContainer = LocalContext.current.appContainer

    NavHost(
        navController = navController,
        startDestination = Routes.cocktails,
        modifier = modifier
    ) {
        composable(Routes.cocktails) {
            val cocktailListViewModel = viewModel(
                modelClass = CocktailListViewModel::class.java,
                factory = AppViewModelProvider.cocktailListFactory(appContainer)
            )
            CocktailListScreen(
                viewModel = cocktailListViewModel,
                onOpenDetail = { id ->
                    navController.navigate(Routes.cocktailDetail(CocktailSource.REMOTE, id))
                }
            )
        }
        composable(Routes.ranking) {
            val rankingViewModel = viewModel(
                modelClass = RankingViewModel::class.java,
                factory = AppViewModelProvider.rankingFactory(appContainer)
            )
            RankingScreen(rankingViewModel)
        }
        composable(Routes.cellar) {
            val cellarViewModel = viewModel(
                modelClass = CellarViewModel::class.java,
                factory = AppViewModelProvider.cellarFactory(appContainer)
            )
            CellarScreen(
                viewModel = cellarViewModel,
                onOpenDetail = { source, id ->
                    navController.navigate(Routes.cocktailDetail(source, id))
                }
            )
        }
        composable(Routes.bacTest) {
            val bacTestViewModel = viewModel(
                modelClass = BacTestViewModel::class.java,
                factory = AppViewModelProvider.bacFactory(appContainer)
            )
            BacTestScreen(bacTestViewModel)
        }
        composable(Routes.createCocktail) {
            val createCocktailViewModel = viewModel(
                modelClass = CreateCocktailViewModel::class.java,
                factory = AppViewModelProvider.createCocktailFactory(appContainer)
            )
            CreateCocktailScreen(
                viewModel = createCocktailViewModel,
                onOpenDetail = { id ->
                    navController.navigate(Routes.cocktailDetail(CocktailSource.LOCAL, id.toString()))
                }
            )
        }
        composable(
            route = Routes.cocktailDetail,
            arguments = listOf(
                navArgument("source") { type = NavType.StringType },
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val source = Routes.sourceFromRouteArgument(
                backStackEntry.arguments?.getString("source")
            )
            val cocktailId = backStackEntry.arguments?.getString("id").orEmpty()
            val detailViewModel = viewModel(
                key = "cocktail_detail_${source.name}_$cocktailId",
                modelClass = CocktailDetailViewModel::class.java,
                factory = AppViewModelProvider.cocktailDetailFactory(
                    appContainer = appContainer,
                    source = source,
                    id = cocktailId
                )
            )
            CocktailDetailScreen(
                viewModel = detailViewModel,
                onBack = navController::navigateUp
            )
        }
    }
}
