package com.stathis.diarycomposeapp.navigation

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.stathis.auth.navigation.authenticationRoute
import com.stathis.diarycomposeapp.presentation.screens.write.WriteScreen
import com.stathis.diarycomposeapp.presentation.screens.write.WriteViewModel
import com.stathis.navigation.homeRoute
import com.stathis.util.Screen
import com.stathis.util.WRITE_SCREEN_ARG_KEY
import com.stathis.util.model.Mood

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

@OptIn(ExperimentalFoundationApi::class)
fun NavGraphBuilder.writeRoute(
    onBackPressed: () -> Unit
) {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = WRITE_SCREEN_ARG_KEY) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {
        val context = LocalContext.current
        val viewModel: WriteViewModel = hiltViewModel()
        val uiState = viewModel.uiState
        val pagerState = rememberPagerState(pageCount = { Mood.entries.size })
        val galleryState = viewModel.galleryState
        val pageNumber by remember {
            derivedStateOf { pagerState.currentPage }
        }

        LaunchedEffect(key1 = uiState) {
            Log.d("Selected Diary", "${uiState.selectedDiaryId}")
        }

        WriteScreen(
            uiState = uiState,
            moodName = {
                Mood.entries[pageNumber].name
            },
            pagerState = pagerState,
            galleryState = galleryState,
            onTitleChanged = {
                viewModel.setTitle(title = it)
            },
            onDescriptionChanged = {
                viewModel.setDescription(description = it)
            },
            onBackPressed = onBackPressed,
            onDeleteConfirm = {
                viewModel.deleteDiary(
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Deleted!",
                            Toast.LENGTH_SHORT
                        ).show()
                        onBackPressed()
                    },
                    onError = { errorMessage ->
                        Toast.makeText(
                            context,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },
            onSaveBtnClick = {
                viewModel.upsertDiary(
                    diary = it.apply { mood = Mood.entries[pageNumber].name },
                    onSuccess = onBackPressed,
                    onError = { errorMessage ->
                        Toast.makeText(
                            context,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                )
            },
            onDateTimeUpdated = {
                viewModel.updateDateTime(zonedDateTime = it)
            },
            onImageSelect = {
                val type = context.contentResolver.getType(it)?.split("/")?.last() ?: "jpg"
                viewModel.addImage(
                    image = it,
                    imageType = type
                )
            },

            onAddBtnClicked = {},
            onImageDeleteClicked = { galleryState.removeImage(it) }
        )
    }
}