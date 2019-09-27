package com.nasa.rendezvous.application

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.nasa.rendezvous.R
import com.nasa.rendezvous.model.localdatabasemodel.ImageDatabase
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso

class App : Application() {

    private lateinit var refWatcher: RefWatcher
    companion object {
        @Volatile
        var database: ImageDatabase? = null

        fun getRefWatcher(context: Context): RefWatcher? {
            val application = context.applicationContext as App
            return application.refWatcher
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        refWatcher = LeakCanary.install(this)

        // Picasso image cache
        val builder = Picasso.Builder(this)
        builder.downloader(OkHttp3Downloader(this, Long.MAX_VALUE))
        val built = builder.build()
        built.setIndicatorsEnabled(false)
        built.isLoggingEnabled = true
        Picasso.setSingletonInstance(built)

        database = Room.databaseBuilder(
            applicationContext,
            ImageDatabase::class.java, applicationContext.getString(R.string.database_name)
        )
            .fallbackToDestructiveMigration().build()
    }
}