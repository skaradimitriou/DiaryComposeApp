package com.stathis.diarycomposeapp.data.repository

import com.stathis.diarycomposeapp.model.Diary
import com.stathis.diarycomposeapp.util.RequestState
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

typealias Diaries = RequestState<Map<LocalDate, List<Diary>>>

interface MongoRepository {

    fun configureTheRealm()

    fun getAllDiaries(): Flow<Diaries>
}