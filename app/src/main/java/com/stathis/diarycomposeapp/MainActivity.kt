package com.stathis.diarycomposeapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.stathis.diarycomposeapp.data.database.ImageToDeleteDao
import com.stathis.diarycomposeapp.data.database.ImagesToUploadDao
import com.stathis.diarycomposeapp.navigation.Screen
import com.stathis.diarycomposeapp.navigation.SetupNavGraph
import com.stathis.ui.theme.DiaryComposeAppTheme
import com.stathis.diarycomposeapp.util.retryDeleteImageToFirebase
import com.stathis.diarycomposeapp.util.retryUploadingImageToFirebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var imagesToUploadDao: ImagesToUploadDao

    @Inject
    lateinit var imageToDeleteDao: ImageToDeleteDao

    private var keepSplashOpened = true

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashOpened
        }

        FirebaseApp.initializeApp(this)

        setContent {
            DiaryComposeAppTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    startDestination = Screen.Authentication.route,
                    navController = navController,
                    onDataLoaded = {
                        keepSplashOpened = false
                    }
                )
            }
        }

        cleanupCheck(
            scope = lifecycleScope,
            imagesToUploadDao = imagesToUploadDao
        )
    }

    private fun cleanupCheck(
        scope: CoroutineScope,
        imagesToUploadDao: ImagesToUploadDao
    ) {
        scope.launch(Dispatchers.IO) {
            val result = imagesToUploadDao.getAllImages()
            result.forEach { imageToUpload ->
                retryUploadingImageToFirebase(
                    imageToUpload = imageToUpload,
                    onSuccess = {
                        scope.launch(Dispatchers.IO) {
                            imagesToUploadDao.cleanupImage(imageToUpload.id)
                        }
                    }
                )
            }

            val result2 = imageToDeleteDao.getAllImages()
            result2.forEach { imageToDelete ->
                retryDeleteImageToFirebase(
                    imageToDelete = imageToDelete,
                    onSuccess = {
                        scope.launch(Dispatchers.IO) {
                            imagesToUploadDao.cleanupImage(imageToDelete.id)
                        }
                    }
                )
            }
        }
    }
}
