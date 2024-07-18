package com.stathis.diarycomposeapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stathis.diarycomposeapp.data.database.entity.ImageToDelete
import com.stathis.diarycomposeapp.data.database.entity.ImageToUpload

@Database(
    entities = [ImageToUpload::class, ImageToDelete::class],
    version = 4,
    exportSchema = false
)

abstract class ImagesDatabase : RoomDatabase() {

    abstract fun imageToUploadDao(): ImagesToUploadDao

    abstract fun imageToDeleteDao(): ImageToDeleteDao
}