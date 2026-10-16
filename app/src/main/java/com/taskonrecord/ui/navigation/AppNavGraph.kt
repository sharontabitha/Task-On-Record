package com.taskonrecord.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taskonrecord.ui.screens.*

object Routes {
    const val VILLAGE = "village"
    const val HOUSE = "house/{categoryId}"
    const val TASK_EDIT = "task_edit/{categoryId}/{taskId}"
    const val ADD_HOUSE = "add_house"

    fun house(categoryId: Long) = "house/$categoryId"
    fun taskEdit(categoryId: Long, taskId: Long = -1L) = "task_edit/$categoryId/$taskId"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.VILLAGE
    ) {
        // Village map (main screen)
        composable(Routes.VILLAGE) {
            VillageScreen(
                onHouseClick = { categoryId ->
                    navController.navigate(Routes.house(categoryId))
                },
                onAddHouse = {
                    navController.navigate(Routes.ADD_HOUSE)
                }
            )
        }

        // House interior (task list for a category)
        composable(
            route = Routes.HOUSE,
            arguments = listOf(navArgument("categoryId") { type = NavType.LongType })
        ) {
            HouseScreen(
                onBack = { navController.popBackStack() },
                onAddTask = { categoryId ->
                    navController.navigate(Routes.taskEdit(categoryId))
                },
                onEditTask = { categoryId, taskId ->
                    navController.navigate(Routes.taskEdit(categoryId, taskId))
                }
            )
        }

        // Task create/edit
        composable(
            route = Routes.TASK_EDIT,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.LongType },
                navArgument("taskId") { type = NavType.LongType; defaultValue = -1L }
            )
        ) {
            TaskEditScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        // Add new house
        composable(Routes.ADD_HOUSE) {
            AddHouseScreen(
                onBack = { navController.popBackStack() },
                onAdded = { navController.popBackStack() }
            )
        }
    }
}
