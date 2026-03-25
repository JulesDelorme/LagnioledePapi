package com.supdevinci.lagnioledepapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocalBar
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.supdevinci.lagnioledepapi.model.CocktailSource
import com.supdevinci.lagnioledepapi.view.BacTestScreen
import com.supdevinci.lagnioledepapi.view.CocktailDetailScreen
import com.supdevinci.lagnioledepapi.view.CocktailListScreen
import com.supdevinci.lagnioledepapi.view.CreateCocktailScreen
import com.supdevinci.lagnioledepapi.view.RankingScreen
import com.supdevinci.lagnioledepapi.view.theme.LaGnioleDePapiTheme
import com.supdevinci.lagnioledepapi.viewmodel.AppViewModelProvider
import com.supdevinci.lagnioledepapi.viewmodel.BacTestViewModel
import com.supdevinci.lagnioledepapi.viewmodel.CocktailDetailViewModel
import com.supdevinci.lagnioledepapi.viewmodel.CocktailListViewModel
import com.supdevinci.lagnioledepapi.viewmodel.CreateCocktailViewModel
import com.supdevinci.lagnioledepapi.viewmodel.RankingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LaGnioleDePapiTheme {
                LaGnioleApp()
            }
        }
    }
}

private data class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private object Routes {
    const val cocktails = "cocktails"
    const val ranking = "ranking"
    const val bacTest = "bac_test"
    const val createCocktail = "create_cocktail"
    const val cocktailDetail = "cocktail_detail/{source}/{id}"

    fun cocktailDetail(source: CocktailSource, id: String): String =
        "cocktail_detail/${source.name.lowercase()}/$id"
}

@Composable
private fun LaGnioleApp() {
    val appContainer = LocalContext.current.appContainer
    val navController = rememberNavController()
    val destinations = listOf(
        TopLevelDestination(Routes.cocktails, "Cocktails", Icons.Outlined.LocalBar),
        TopLevelDestination(Routes.ranking, "Classement", Icons.Outlined.EmojiEvents),
        TopLevelDestination(Routes.bacTest, "Test", Icons.Outlined.Science),
        TopLevelDestination(Routes.createCocktail, "Créer", Icons.Outlined.Add)
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = destinations.any { destination ->
        currentDestination?.hierarchy?.any { it.route == destination.route } == true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    destinations.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.cocktails,
            modifier = Modifier.padding(innerPadding)
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
                val source = when (backStackEntry.arguments?.getString("source")) {
                    "local" -> CocktailSource.LOCAL
                    else -> CocktailSource.REMOTE
                }
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
}
