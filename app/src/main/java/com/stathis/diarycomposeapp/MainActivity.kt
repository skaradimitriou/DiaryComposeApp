package com.stathis.diarycomposeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.stathis.diarycomposeapp.data.repository.MongoDb
import com.stathis.diarycomposeapp.navigation.Screen
import com.stathis.diarycomposeapp.navigation.SetupNavGraph
import com.stathis.diarycomposeapp.ui.theme.DiaryComposeAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            DiaryComposeAppTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    startDestination = Screen.Authentication.route,
                    navController = navController
                )
            }
        }
    }
}
