package com.stathis.diarycomposeapp.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.stathis.diarycomposeapp.data.repository.MongoDb
import com.stathis.diarycomposeapp.presentation.components.DisplayAlertDialog
import com.stathis.diarycomposeapp.presentation.screens.auth.AuthenticationScreen
import com.stathis.diarycomposeapp.presentation.screens.home.HomeScreen
import com.stathis.diarycomposeapp.util.WRITE_SCREEN_ARG_KEY
import kotlinx.coroutines.launch

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
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var dialogOpened by remember { mutableStateOf(false) }

        HomeScreen(
            drawerState = drawerState,
            onMenuClicked = {
                scope.launch {
                    drawerState.open()
                }
            },
            onSignOutClick = {
                dialogOpened = true
            },
            onNavigateToWriteScreen = navigateToWrite
        )

        LaunchedEffect(key1 = Unit) {
            MongoDb.configureTheRealm()
        }

        DisplayAlertDialog(
            title = "Sign Out",
            message = "Are you sure you want to sign out from your google account?",
            dialogOpened = dialogOpened,
            onYesClicked = {
                //sign out user here.
                // I don't want to sign out the user
            },
            closeDialog = {
                dialogOpened = false
            })
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