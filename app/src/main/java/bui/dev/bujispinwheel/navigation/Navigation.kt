package bui.dev.bujispinwheel.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import bui.dev.bujispinwheel.ui.home.screens.HomeScreen
import bui.dev.bujispinwheel.ui.wheelspin.components.SpinWheel
import bui.dev.bujispinwheel.ui.wheelspin.screens.EditWheelListScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController)
        }
        composable("spin_wheel") {
            SpinWheel(navController = navController)
        }
        composable(
            route = "edit_wheel_list/{listId}",
            arguments = listOf(navArgument("listId") { type = NavType.StringType })
        ) { backStackEntry ->
            EditWheelListScreen(
                listId = backStackEntry.arguments?.getString("listId") ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
} 