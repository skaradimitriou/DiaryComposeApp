package com.stathis.diarycomposeapp.presentation.screens.write

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.stathis.diarycomposeapp.model.Diary

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WriteScreen(
    selectedDiary: Diary?,
    pagerState: PagerState,
    onBackPressed: () -> Unit,
    onDeleteConfirm: () -> Unit
) {
    Scaffold(
        topBar = {
            WriteTopBar(
                selectedDiary = selectedDiary,
                onBackPressed = onBackPressed,
                onDeleteConfirm = onDeleteConfirm
            )
        },
        content = {
            WriteContent(
                paddingValues = it,
                title = "",
                onTitleChanged = {

                },
                description = "",
                onDescriptionChanged = {

                },
                pagerState = pagerState
            )
        }
    )
}