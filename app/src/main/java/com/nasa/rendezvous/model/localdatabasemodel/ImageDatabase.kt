package com.nasa.rendezvous.model.localdatabasemodel

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nasa.rendezvous.model.NasaImages


@Database(entities = [NasaImages::class], version = 1)
abstract class ImageDatabase : RoomDatabase() {
    abstract fun imageDao(): ImagesDao
}