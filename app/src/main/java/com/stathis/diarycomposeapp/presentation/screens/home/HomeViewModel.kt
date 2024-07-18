package com.stathis.diarycomposeapp.presentation.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.stathis.diarycomposeapp.connectivity.ConnectivityObserver
import com.stathis.diarycomposeapp.connectivity.NetworkConnectivityObserver
import com.stathis.diarycomposeapp.data.database.ImageToDeleteDao
import com.stathis.diarycomposeapp.data.database.entity.ImageToDelete
import com.stathis.diarycomposeapp.data.repository.Diaries
import com.stathis.diarycomposeapp.data.repository.MongoDb
import com.stathis.diarycomposeapp.model.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val connectivity: NetworkConnectivityObserver,
    private val imageToDeleteDao: ImageToDeleteDao
) : ViewModel() {

    private var network by mutableStateOf(ConnectivityObserver.Status.Unavailable)


    var diaries: MutableState<Diaries> = mutableStateOf(RequestState.Idle)

    init {
        observeAllDiaries()
        viewModelScope.launch {
            connectivity.observe().collect {
                network = it
            }
        }
    }

    private fun observeAllDiaries() {
        viewModelScope.launch {
            MongoDb.getAllDiaries().collect { result ->
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