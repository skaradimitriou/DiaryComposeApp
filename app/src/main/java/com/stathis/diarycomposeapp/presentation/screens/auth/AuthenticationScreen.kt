package com.stathis.diarycomposeapp.presentation.screens.auth

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthenticationScreen(
    onButtonClicked: () -> Unit
) {
    Scaffold(
        content = {
            AuthenticationContent(onButtonClicked = onButtonClicked)
        }
    )
}