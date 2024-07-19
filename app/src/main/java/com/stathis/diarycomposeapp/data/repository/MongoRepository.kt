package com.stathis.diarycomposeapp.data.repository

import com.stathis.diarycomposeapp.model.Diary
import com.stathis.diarycomposeapp.model.RequestState
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZonedDateTime

typealias Diaries = RequestState<Map<LocalDate, List<Diary>>>

interface MongoRepository {

    fun configureTheRealm()

    fun getAllDiaries(): Flow<Diaries>

    fun getFilteredDiaries(zonedDateTime: ZonedDateTime): Flow<Diaries>

    fun getSelectedDiary(diaryId: ObjectId): Flow<RequestState<Diary>>

    suspend fun insertNewDiary(diary: Diary): RequestState<Diary>

    suspend fun updateDiary(diary: Diary): RequestState<Diary>

    suspend fun deleteDiary(id: ObjectId): RequestState<Diary>

    suspend fun deleteAllDiaries(): RequestState<Boolean>
}