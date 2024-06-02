package com.example.imagecaptionapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.imagecaptionapp.ui.chooser.ChooserDestination
import com.example.imagecaptionapp.ui.chooser.ImageChooserScreen
import com.example.imagecaptionapp.ui.home.HomeDestination
import com.example.imagecaptionapp.ui.home.HomeScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun InventoryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToImageChooser = { navController.navigate(ChooserDestination.route) }
            )
        }
        composable(route = ChooserDestination.route) {
            ImageChooserScreen(
                navigateBack = { navController.navigateUp() }
            )
        }
    }
}
