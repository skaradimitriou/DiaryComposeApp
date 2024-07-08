package com.stathis.diarycomposeapp.presentation.screens.write

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.stathis.diarycomposeapp.model.Diary
import com.stathis.diarycomposeapp.model.Mood
import java.time.LocalDateTime
import java.time.ZonedDateTime

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WriteScreen(
    uiState: UiState,
    moodName: () -> String,
    pagerState: PagerState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onBackPressed: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onDateTimeUpdated : (ZonedDateTime) -> Unit,
    onSaveBtnClick : (Diary) -> Unit
) {
    LaunchedEffect(key1 = uiState.mood) {
        pagerState.scrollToPage(Mood.valueOf(uiState.mood.name).ordinal)
    }

    Scaffold(
        topBar = {
            WriteTopBar(
                selectedDiary = uiState.selectedDiary,
                moodName = moodName,
                onBackPressed = onBackPressed,
                onDeleteConfirm = onDeleteConfirm,
                onUpdatedDateTime = onDateTimeUpdated
            )
        },
        content = {
            WriteContent(
                uiState = uiState,
                paddingValues = it,
                title = uiState.title,
                onTitleChanged = onTitleChanged,
                description = uiState.description,
                onDescriptionChanged = onDescriptionChanged,
                pagerState = pagerState,
                onSaveBtnClick = onSaveBtnClick
            )
        }
    )
}