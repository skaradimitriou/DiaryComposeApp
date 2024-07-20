package com.stathis.diarycomposeapp.presentation.screens.write

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import com.stathis.diarycomposeapp.data.database.ImageToDeleteDao
import com.stathis.diarycomposeapp.data.database.ImagesToUploadDao
import com.stathis.diarycomposeapp.data.database.entity.ImageToDelete
import com.stathis.diarycomposeapp.data.database.entity.ImageToUpload
import com.stathis.diarycomposeapp.data.repository.MongoDb
import com.stathis.diarycomposeapp.model.Diary
import com.stathis.ui.GalleryImage
import com.stathis.ui.GalleryState
import com.stathis.diarycomposeapp.model.Mood
import com.stathis.diarycomposeapp.model.RequestState
import com.stathis.diarycomposeapp.util.WRITE_SCREEN_ARG_KEY
import com.stathis.diarycomposeapp.util.fetchImagesFromFirebase
import com.stathis.diarycomposeapp.util.toRealmInstant
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val imagesToUploadDao: ImagesToUploadDao,
    private val imagesToDeleteDao: ImageToDeleteDao
) : ViewModel() {

    val galleryState = GalleryState()

    var uiState by mutableStateOf(UiState())
        private set

    init {
        getDiaryIdArgument()
        fetchSelectedDiary()
    }

    private fun getDiaryIdArgument() {
        uiState = uiState.copy(
            selectedDiaryId = savedStateHandle.get<String>(
                key = WRITE_SCREEN_ARG_KEY
            )
        )
    }

    private fun fetchSelectedDiary() {
        if (uiState.selectedDiaryId != null) {
            viewModelScope.launch {
                MongoDb.getSelectedDiary(
                    diaryId = ObjectId.Companion.from(uiState.selectedDiaryId.toString())
                ).catch {
                    emit(RequestState.Error(Exception("Diary is already deleted.")))
                }.collect { diary ->
                    if (diary is RequestState.Success) {
                        setTitle(title = diary.data.title)
                        setSelectedDiary(diary = diary.data)
                        setDescription(description = diary.data.description)
                        setMood(mood = Mood.valueOf(diary.data.mood))

                        fetchImagesFromFirebase(
                            remoteImagePaths = diary.data.images,
                            onImageDownload = { downloadedImage ->
                                galleryState.addImage(
                                    GalleryImage(
                                        image = downloadedImage,
                                        remoteImagePath = extractRemoteImagePath(
                                            fullImageUrl = downloadedImage.toString()
                                        )
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    fun setTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    private fun setSelectedDiary(diary: Diary) {
        uiState = uiState.copy(selectedDiary = diary)
    }

    fun setDescription(description: String) {
        uiState = uiState.copy(description = description)
    }

    private fun setMood(mood: Mood) {
        uiState = uiState.copy(mood = mood)
    }

    fun updateDateTime(zonedDateTime: ZonedDateTime) {
        uiState = uiState.copy(updatedDateTime = zonedDateTime.toInstant().toRealmInstant())
    }

    fun upsertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.selectedDiaryId != null) {
                updateDiary(
                    diary = diary,
                    onSuccess = onSuccess,
                    onError = onError
                )
            } else {
                insertDiary(
                    diary = diary,
                    onSuccess = onSuccess,
                    onError = onError
                )
            }
        }
    }

    private suspend fun insertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDb.insertNewDiary(diary = diary.apply {
            if (uiState.updatedDateTime != null) {
                date = uiState.updatedDateTime!!
            }
        })
        withContext(Dispatchers.Main) {
            when (result) {
                is RequestState.Success -> {
                    uploadImagesToFirebase()
                    onSuccess()
                }

                is RequestState.Error -> onError(result.error.message.toString())
                else -> Unit
            }
        }
    }

    private suspend fun updateDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDb.updateDiary(
            diary = diary.apply {
                _id = ObjectId.Companion.from(uiState.selectedDiaryId!!)
                date = if (uiState.updatedDateTime != null) {
                    uiState.updatedDateTime!!
                } else {
                    uiState.selectedDiary!!.date
                }
            }
        )
        withContext(Dispatchers.Main) {
            when (result) {
                is RequestState.Success -> {
                    uploadImagesToFirebase()
                    deleteImagesFromFirebase()
                    onSuccess()
                }

                is RequestState.Error -> onError(result.error.message.toString())
                else -> Unit
            }
        }
    }

    fun deleteDiary(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.selectedDiaryId != null) {
                when (val result =
                    MongoDb.deleteDiary(id = ObjectId.Companion.from(uiState.selectedDiaryId!!))) {
                    is RequestState.Success -> {
                        withContext(Dispatchers.Main) {
                            uiState.selectedDiary?.images?.let { deleteImagesFromFirebase(images = it) }
                            onSuccess()
                        }
                    }

                    is RequestState.Error -> {
                        withContext(Dispatchers.Main) {
                            onError(result.error.message.toString())
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    fun addImage(image: Uri, imageType: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val imageName = image.lastPathSegment
        val currentTime = System.currentTimeMillis()
        val remoteImagePath = "images/$uid/$imageName-$currentTime.$imageType"
        galleryState.addImage(
            GalleryImage(
                image = image,
                remoteImagePath = remoteImagePath
            )
        )
    }

    private fun uploadImagesToFirebase() {
        val storage = FirebaseStorage.getInstance().reference
        galleryState.images.forEach { galleryImage ->
            val imagePath = storage.child(galleryImage.remoteImagePath)
            imagePath.putFile(galleryImage.image)
                .addOnProgressListener {
                    val sessionUri = it.uploadSessionUri
                    if (sessionUri != null) {
                        viewModelScope.launch(Dispatchers.IO) {
                            imagesToUploadDao.addImageToUpload(
                                ImageToUpload(
                                    remoteImagePath = galleryImage.remoteImagePath,
                                    imageUri = galleryImage.image.toString(),
                                    sessionUri = sessionUri.toString()
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun extractRemoteImagePath(fullImageUrl: String): String {
        val chunks = fullImageUrl.split("%2F")
        val imageName = chunks[2].split("?").first()
        return "images/${Firebase.auth.currentUser?.uid}/$imageName"
    }

    private fun deleteImagesFromFirebase(images: List<String>? = null) {
        val storage = FirebaseStorage.getInstance().reference
        images?.forEach { remoteImage ->
            storage.child(remoteImage).delete()
                .addOnFailureListener {
                    viewModelScope.launch(Dispatchers.IO) {
                        imagesToDeleteDao.addImageToDelete(
                            ImageToDelete(
                                remoteImagePath = remoteImage
                            )
                        )
                    }
                }
        } ?: run {
            galleryState.imagesToBeDeleted.map { it.remoteImagePath }.forEach { remotePath ->
                storage.child(remotePath).delete().addOnFailureListener {
                    viewModelScope.launch(Dispatchers.IO) {
                        imagesToDeleteDao.addImageToDelete(
                            ImageToDelete(
                                remoteImagePath = remotePath
                            )
                        )
                    }
                }
            }
        }
    }
}

data class UiState(
    val selectedDiaryId: String? = null,
    val selectedDiary: Diary? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral,
    val updatedDateTime: RealmInstant? = null
)