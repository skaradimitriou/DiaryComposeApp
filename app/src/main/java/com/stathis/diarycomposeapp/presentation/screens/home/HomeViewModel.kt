package com.stathis.diarycomposeapp.presentation.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.stathis.util.connectivity.ConnectivityObserver
import com.stathis.util.connectivity.NetworkConnectivityObserver
import com.stathis.mongo.database.ImageToDeleteDao
import com.stathis.mongo.database.entity.ImageToDelete
import com.stathis.mongo.repository.Diaries
import com.stathis.mongo.repository.MongoDb
import com.stathis.util.model.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val connectivity: NetworkConnectivityObserver,
    private val imageToDeleteDao: ImageToDeleteDao
) : ViewModel() {

    private lateinit var allDiariesJob: Job
    private lateinit var filteredDiariesJob: Job

    private var network by mutableStateOf(ConnectivityObserver.Status.Unavailable)
    var diaries: MutableState<Diaries> = mutableStateOf(RequestState.Idle)

    var dateIsSelected by mutableStateOf(false)
        private set

    init {
        getDiaries()
        viewModelScope.launch {
            connectivity.observe().collect {
                network = it
            }
        }
    }

    fun getDiaries(zonedDateTime: ZonedDateTime? = null) {
        dateIsSelected = zonedDateTime != null
        diaries.value = RequestState.Loading

        if (dateIsSelected && zonedDateTime != null) {
            observeFilteredDiaries(zonedDateTime)
        } else {
            observeAllDiaries()
        }
    }

    private fun observeAllDiaries() {
        allDiariesJob = viewModelScope.launch {
            if (::filteredDiariesJob.isInitialized) {
                filteredDiariesJob.cancelAndJoin()
            }
            MongoDb.getAllDiaries().collect { result ->
                diaries.value = result
            }
        }
    }

    private fun observeFilteredDiaries(zonedDateTime: ZonedDateTime) {
        filteredDiariesJob = viewModelScope.launch(Dispatchers.IO) {
            if (::allDiariesJob.isInitialized) {
                allDiariesJob.cancelAndJoin()
            }
            MongoDb.getFilteredDiaries(zonedDateTime).collect { result ->
                diaries.value = result
            }
        }
    }

    fun deleteAllDiaries(
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (network == ConnectivityObserver.Status.Available) {
                val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
                val imagesDirectory = "images/$userId"
                val firebaseStorage = FirebaseStorage.getInstance().reference
                firebaseStorage.child(imagesDirectory)
                    .listAll()
                    .addOnSuccessListener {
                        it.items.forEach { ref ->
                            val imagePath = "images/$userId/${ref.name}"
                            firebaseStorage.child(imagePath).delete().addOnFailureListener {
                                viewModelScope.launch(Dispatchers.IO) {
                                    imageToDeleteDao.addImageToDelete(
                                        ImageToDelete(remoteImagePath = imagePath)
                                    )
                                }
                            }
                        }

                        viewModelScope.launch(Dispatchers.IO) {
                            val result = MongoDb.deleteAllDiaries()
                            if (result is RequestState.Success) {
                                withContext(Dispatchers.Main) {
                                    onSuccess()
                                }
                            } else if (result is RequestState.Error) {
                                withContext(Dispatchers.Main) {
                                    onError(result.error)
                                }
                            }
                        }
                    }.addOnFailureListener { onError(it) }
            } else {
                onError(Exception("No Internet connection."))
            }
        }
    }
}