package com.stathis.diarycomposeapp.presentation.screens.home

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    onMenuClicked: () -> Unit,
    onNavigateToWriteScreen: () -> Unit
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                title = "Home Bar Title",
                onMenuClick = onMenuClicked
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToWriteScreen) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "New Diary Icon"
                )
            }
        },
        content = {

        }
    )
}