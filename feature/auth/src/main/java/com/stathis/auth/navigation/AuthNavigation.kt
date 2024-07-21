package com.stathis.auth.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.stathis.auth.AuthenticationScreen
import com.stathis.util.Screen

fun NavGraphBuilder.authenticationRoute(
    goToHomeScreen: () -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(route = Screen.Authentication.route) {
        LaunchedEffect(key1 = Unit) {
            onDataLoaded()
        }

        AuthenticationScreen(
            onButtonClicked = {
                goToHomeScreen.invoke()
            },
            onSuccessFullAuth = {
                goToHomeScreen.invoke()
            },
            onFailedAuthAttempt = {

            }
        )
    }
}