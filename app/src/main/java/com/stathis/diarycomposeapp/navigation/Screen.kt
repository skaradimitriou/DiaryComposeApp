package com.stathis.diarycomposeapp.navigation

import com.stathis.diarycomposeapp.util.WRITE_SCREEN_ARG_KEY

sealed class Screen(val route: String) {
    data object Authentication : Screen(route = "authentication_screen")
    data object Home : Screen(route = "home_screen")
    data object Write :
        Screen(route = "write_screen?$WRITE_SCREEN_ARG_KEY={$WRITE_SCREEN_ARG_KEY}") {
        fun passDiaryId(diaryId: String) = "write_screen?$WRITE_SCREEN_ARG_KEY={$diaryId}"
    }
}