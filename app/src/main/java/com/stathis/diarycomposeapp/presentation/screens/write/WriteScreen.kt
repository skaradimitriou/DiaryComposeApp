package com.stathis.diarycomposeapp.presentation.screens.write

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.stathis.diarycomposeapp.model.Diary

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WriteScreen(
    selectedDiary: Diary?,
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

        }
    )
}