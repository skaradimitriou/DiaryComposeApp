package com.stathis.write.navigation

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.stathis.util.Screen
import com.stathis.util.WRITE_SCREEN_ARG_KEY
import com.stathis.util.model.Mood
import com.stathis.write.WriteScreen
import com.stathis.write.WriteViewModel

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
                        android.widget.Toast.makeText(
                            context,
                            "Deleted!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        onBackPressed()
                    },
                    onError = { errorMessage ->
                        android.widget.Toast.makeText(
                            context,
                            errorMessage,
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },
            onSaveBtnClick = {
                viewModel.upsertDiary(
                    diary = it.apply { mood = Mood.entries[pageNumber].name },
                    onSuccess = onBackPressed,
                    onError = { errorMessage ->
                        android.widget.Toast.makeText(
                            context,
                            errorMessage,
                            android.widget.Toast.LENGTH_SHORT
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