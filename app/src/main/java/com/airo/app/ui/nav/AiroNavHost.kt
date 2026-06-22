package com.airo.app.ui.nav

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.airo.app.AiroApplication
import com.airo.app.ui.home.HomeScreen
import com.airo.app.ui.home.HomeViewModel
import com.airo.app.ui.scan.ScanScreen
import com.airo.app.ui.scan.ScanViewModel
import com.airo.app.ui.space.SpaceDetailScreen
import com.airo.app.ui.space.SpaceDetailViewModel

private const val ROUTE_HOME = "home"
private const val ROUTE_SPACE = "space/{spaceId}/{spaceName}"
private const val ROUTE_SCAN = "scan/{spaceId}/{spaceName}"

private fun spaceRoute(spaceId: String, spaceName: String) = "space/$spaceId/${Uri.encode(spaceName)}"
private fun scanRoute(spaceId: String, spaceName: String) = "scan/$spaceId/${Uri.encode(spaceName)}"

@Composable
fun AiroNavHost() {
    val navController = rememberNavController()
    val container = (LocalContext.current.applicationContext as AiroApplication).container
    val repository = container.repository

    NavHost(navController = navController, startDestination = ROUTE_HOME) {
        composable(ROUTE_HOME) {
            val viewModel: HomeViewModel = viewModel(
                factory = viewModelFactory { initializer { HomeViewModel(repository) } },
            )
            HomeScreen(
                viewModel = viewModel,
                onSpaceClick = { space -> navController.navigate(spaceRoute(space.id, space.name)) },
            )
        }

        composable(
            ROUTE_SPACE,
            arguments = listOf(navArgument("spaceId") { type = NavType.StringType }, navArgument("spaceName") { type = NavType.StringType }),
        ) { backStackEntry ->
            val spaceId = backStackEntry.arguments?.getString("spaceId").orEmpty()
            val spaceName = backStackEntry.arguments?.getString("spaceName").orEmpty()
            val viewModel: SpaceDetailViewModel = viewModel(
                key = spaceId,
                factory = viewModelFactory { initializer { SpaceDetailViewModel(repository, spaceId) } },
            )
            SpaceDetailScreen(
                spaceName = spaceName,
                viewModel = viewModel,
                onScanClick = { navController.navigate(scanRoute(spaceId, spaceName)) },
            )
        }

        composable(
            ROUTE_SCAN,
            arguments = listOf(navArgument("spaceId") { type = NavType.StringType }, navArgument("spaceName") { type = NavType.StringType }),
        ) { backStackEntry ->
            val spaceId = backStackEntry.arguments?.getString("spaceId").orEmpty()
            val spaceName = backStackEntry.arguments?.getString("spaceName").orEmpty()
            val viewModel: ScanViewModel = viewModel(
                key = "$spaceId-scan",
                factory = viewModelFactory { initializer { ScanViewModel(repository, spaceId, spaceName) } },
            )
            ScanScreen(
                spaceName = spaceName,
                viewModel = viewModel,
                onDone = { navController.popBackStack() },
            )
        }
    }
}
