package com.stathis.diarycomposeapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.stathis.diarycomposeapp.presentation.screens.auth.AuthenticationScreen
import com.stathis.diarycomposeapp.presentation.screens.home.HomeScreen
import com.stathis.diarycomposeapp.util.WRITE_SCREEN_ARG_KEY

@Composable
fun SetupNavGraph(startDestination: String, navController: NavHostController) {
    NavHost(
        startDestination = startDestination,
        navController = navController
    ) {
        authenticationRoute {
            navController.navigate(Screen.Home.route)
        }
        homeRoute(
            navigateToWrite = {
                navController.navigate(Screen.Write.route)
            }
        )
        writeRoute()
    }
}

fun NavGraphBuilder.authenticationRoute(
    goToHomeScreen: () -> Unit
) {
    composable(route = Screen.Authentication.route) {
        AuthenticationScreen(
            onButtonClicked = {
                goToHomeScreen.invoke()
            }
        )
    }
}

fun NavGraphBuilder.homeRoute(
    navigateToWrite: () -> Unit
) {
    composable(route = Screen.Home.route) {
        HomeScreen(
            onMenuClicked = { },
            onNavigateToWriteScreen = navigateToWrite
        )
    }
}

fun NavGraphBuilder.writeRoute() {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = WRITE_SCREEN_ARG_KEY) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {

    }
}