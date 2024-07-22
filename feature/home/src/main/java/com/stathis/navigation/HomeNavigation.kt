package com.stathis.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.stathis.home.HomeScreen
import com.stathis.home.HomeViewModel
import com.stathis.mongo.repository.MongoDb
import com.stathis.ui.components.DisplayAlertDialog
import com.stathis.util.Screen
import com.stathis.util.model.RequestState
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun NavGraphBuilder.homeRoute(
    navigateToWrite: () -> Unit,
    navigateToWriteWithArgs: (String) -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(route = Screen.Home.route) {
        val viewModel: HomeViewModel = hiltViewModel()
        val context = LocalContext.current
        val diaries by viewModel.diaries
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var dialogOpened by remember { mutableStateOf(false) }
        var deleteAllDialogOpened by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = diaries) {
            if (diaries !is RequestState.Loading) {
                onDataLoaded()
            }
        }

        HomeScreen(
            diaries = diaries,
            drawerState = drawerState,
            onMenuClicked = {
                scope.launch {
                    drawerState.open()
                }
            },
            dateIsSelected = viewModel.dateIsSelected,
            onDateSelected = { viewModel.getDiaries(it) },
            onDateReset = {
                viewModel.getDiaries()
            },
            onSignOutClick = {
                dialogOpened = true
            },
            onDeleteAllClicked = {
                deleteAllDialogOpened = true
            },
            onNavigateToWriteScreen = navigateToWrite,
            navigateToWriteWithArgs = navigateToWriteWithArgs
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

        DisplayAlertDialog(
            title = "Delete All Diaries",
            message = "Are you sure you want to permantently delete all your diaries?",
            dialogOpened = deleteAllDialogOpened,
            onYesClicked = {
                viewModel.deleteAllDiaries(
                    onSuccess = {
                        scope.launch {
                            android.widget.Toast.makeText(
                                context,
                                "XXX",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                            drawerState.close()
                        }
                    },
                    onError = {
                        val message = if (it.message == "No Internet connection.") {
                            "We need internet to perform this action."
                        } else {
                            it.message
                        }

                        scope.launch {
                            android.widget.Toast.makeText(
                                context,
                                "$message",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                            drawerState.close()
                        }
                    }
                )
            },
            closeDialog = {
                deleteAllDialogOpened = false
            })
    }
}