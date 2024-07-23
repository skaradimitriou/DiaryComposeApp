package com.stathis.write

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.stathis.util.model.Diary
import com.stathis.util.model.Mood
import com.stathis.ui.GalleryImage
import com.stathis.ui.GalleryState
import java.time.ZonedDateTime

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun WriteScreen(
    uiState: UiState,
    moodName: () -> String,
    pagerState: PagerState,
    galleryState: GalleryState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onBackPressed: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onDateTimeUpdated: (ZonedDateTime) -> Unit,
    onAddBtnClicked: () -> Unit,
    onImageSelect: (Uri) -> Unit,
    onSaveBtnClick: (Diary) -> Unit,
    onImageDeleteClicked: (GalleryImage) -> Unit
) {
    var selectedGalleryImage by remember { mutableStateOf<GalleryImage?>(null) }

    LaunchedEffect(key1 = uiState.mood) {
        pagerState.scrollToPage(Mood.valueOf(uiState.mood.name).ordinal)
    }

    Scaffold(topBar = {
        WriteTopBar(
            selectedDiary = uiState.selectedDiary,
            moodName = moodName,
            onBackPressed = onBackPressed,
            onDeleteConfirm = onDeleteConfirm,
            onUpdatedDateTime = onDateTimeUpdated
        )
    }, content = { paddingValues ->
        WriteContent(
            uiState = uiState,
            paddingValues = paddingValues,
            title = uiState.title,
            onTitleChanged = onTitleChanged,
            description = uiState.description,
            onDescriptionChanged = onDescriptionChanged,
            pagerState = pagerState,
            galleryState = galleryState,
            onImageSelect = onImageSelect,
            onImageClicked = { selectedGalleryImage = it },
            onSaveBtnClick = onSaveBtnClick
        )

        AnimatedVisibility(
            visible = selectedGalleryImage != null
        ) {
            Dialog(onDismissRequest = { selectedGalleryImage = null }) {
                if (selectedGalleryImage != null) {
                    ZoomableImage(
                        selectedGalleryImage = selectedGalleryImage!!,
                        onCloseClicked = { selectedGalleryImage = null },
                        onDeleteClicked = {
                            if (selectedGalleryImage != null) {
                                onImageDeleteClicked(selectedGalleryImage!!)
                                selectedGalleryImage = null
                            }
                        }
                    )
                }
            }
        }
    })
}

@Composable
internal fun ZoomableImage(
    selectedGalleryImage: GalleryImage,
    onCloseClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }

    Box(modifier = Modifier.pointerInput(Unit) {
        detectTransformGestures { _, pan, zoom, _ ->
            scale = maxOf(1f, minOf(scale * zoom), 5f)
            val maxX = (size.width * (scale - 1)) / 2
            val minX = -maxX
            offsetX = maxOf(minX, minOf(maxX, offsetX + pan.x))
            val maxY = (size.height * (scale - 1)) / 2
            val minY = -maxY
            offsetY = maxOf(minY, minOf(maxY, offsetY + pan.y))
        }
    }) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = maxOf(.5f, minOf(3f, scale)),
                    scaleY = maxOf(.5f, minOf(3f, scale)),
                    translationX = offsetX,
                    translationY = offsetY,
                ),
            model = ImageRequest.Builder(LocalContext.current)
                .data(selectedGalleryImage.image)
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Fit,
            contentDescription = "Gallery Image"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
        ) {
            Button(onClick = onCloseClicked) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Icon"
                )
                Text(text = "Close")
            }
            Button(onClick = onDeleteClicked) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Icon"
                )
                Text(text = "Delete")
            }
        }
    }
}