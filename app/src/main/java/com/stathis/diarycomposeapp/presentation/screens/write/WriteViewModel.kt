package com.stathis.diarycomposeapp.presentation.screens.write

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stathis.diarycomposeapp.data.repository.MongoDb
import com.stathis.diarycomposeapp.model.Diary
import com.stathis.diarycomposeapp.model.Mood
import com.stathis.diarycomposeapp.util.RequestState
import com.stathis.diarycomposeapp.util.WRITE_SCREEN_ARG_KEY
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.launch

class WriteViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

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
                val diary = MongoDb.getSelectedDiary(
                    diaryId = ObjectId.Companion.from(uiState.selectedDiaryId.toString())
                )

                if (diary is RequestState.Success) {
                    setTitle(title = diary.data.title)
                    setSelectedDiary(diary = diary.data)
                    setDescription(description = diary.data.description)
                    setMood(mood = Mood.valueOf(diary.data.mood))
                }
            }
        }
    }

    fun setTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun setSelectedDiary(diary: Diary) {
        uiState = uiState.copy(selectedDiary = diary)
    }

    fun setDescription(description: String) {
        uiState = uiState.copy(description = description)
    }

    private fun setMood(mood: Mood) {
        uiState = uiState.copy(mood = mood)
    }
}

data class UiState(
    val selectedDiaryId: String? = null,
    val selectedDiary: Diary? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral
)