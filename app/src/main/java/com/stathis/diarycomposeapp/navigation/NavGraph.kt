package com.stathis.diarycomposeapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.stathis.auth.navigation.authenticationRoute
import com.stathis.navigation.homeRoute
import com.stathis.util.Screen
import com.stathis.write.navigation.writeRoute

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SetupNavGraph(
    startDestination: String,
    navController: NavHostController,
    onDataLoaded: () -> Unit
) {
    NavHost(
        startDestination = startDestination,
        navController = navController
    ) {
        authenticationRoute(
            onDataLoaded = onDataLoaded,
            goToHomeScreen = {
                navController.navigate(Screen.Home.route)
            }
        )
        homeRoute(
            navigateToWrite = {
                navController.navigate(Screen.Write.route)
            },

            navigateToWriteWithArgs = {
                navController.navigate(Screen.Write.passDiaryId(diaryId = it))
            },
            onDataLoaded = onDataLoaded
        )

        writeRoute(
            onBackPressed = {
                navController.popBackStack()
            }
        )
    }
}