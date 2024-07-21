package com.stathis.diarycomposeapp.di

import android.content.Context
import androidx.room.Room
import com.stathis.mongo.database.ImagesDatabase
import com.stathis.util.IMAGES_DATABASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideImagesDatabase(
        @ApplicationContext context: Context
    ): ImagesDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = ImagesDatabase::class.java,
            name = IMAGES_DATABASE
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideFirstDao(db: ImagesDatabase) = db.imageToUploadDao()

    @Provides
    @Singleton
    fun provideSecondDao(db: ImagesDatabase) = db.imageToDeleteDao()
}