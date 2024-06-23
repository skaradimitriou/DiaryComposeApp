package com.stathis.diarycomposeapp.presentation.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlin.math.max

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Gallery(
    modifier: Modifier = Modifier,
    images: List<String>,
    imageSize: Dp = 40.dp,
    spaceBetween: Dp = 10.dp,
    imageShape: CornerBasedShape = Shapes().small
) {
    BoxWithConstraints(modifier = modifier) {
        val numberOfVisibleImages = remember {
            derivedStateOf {
                max(
                    a = 0,
                    b = maxWidth.div(spaceBetween + imageSize).toInt().minus(1)
                )
            }
        }

        val remainingImages = remember {
            derivedStateOf {
                images.size - numberOfVisibleImages.value
            }
        }

        Row {
            images.take(numberOfVisibleImages.value).forEach { image ->
                AsyncImage(
                    modifier = Modifier
                        .clip(imageShape)
                        .size(imageSize),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Gallery Image"
                )
                Spacer(modifier = Modifier.width(spaceBetween))
            }

            if (remainingImages.value > 0) {
                LastImageOverlay(
                    imageSize = imageSize,
                    remainingImages = remainingImages.value,
                    imageShape = imageShape
                )
            }
        }
    }
}

@Composable
fun LastImageOverlay(
    imageSize: Dp,
    remainingImages: Int,
    imageShape: CornerBasedShape
) {
    Box(contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier
                .clip(imageShape)
                .size(imageSize),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {}
        Text(
            text = "$remainingImages",
            style = TextStyle(
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
fun GalleryPreview() {
    Gallery(
        images = listOf(
            "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Google_%22G%22_logo.svg/1024px-Google_%22G%22_logo.svg.png"
        )
    )
}