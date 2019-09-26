package com.nasa.rendezvous.application

import android.app.Application
import androidx.room.Room
import com.nasa.rendezvous.model.localdatabasemodel.ImageDatabase
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso

class App : Application() {

    companion object {
        @Volatile
        var database: ImageDatabase? = null
    }

    override fun onCreate() {
        super.onCreate()

        // Picasso image cache
        val builder = Picasso.Builder(this)
        builder.downloader(OkHttp3Downloader(this, Long.MAX_VALUE))
        val built = builder.build()
        built.setIndicatorsEnabled(false)
        built.isLoggingEnabled = true
        Picasso.setSingletonInstance(built)

        database = Room.databaseBuilder(
            applicationContext,
            ImageDatabase::class.java, "country_db"
        )
            .fallbackToDestructiveMigration().build()
    }


}