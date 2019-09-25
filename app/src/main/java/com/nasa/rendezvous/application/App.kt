package com.nasa.rendezvous.application

import android.app.Application
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Picasso image cache
        val builder = Picasso.Builder(this)
        builder.downloader(OkHttp3Downloader(this, Long.MAX_VALUE))
        val built = builder.build()
        built.setIndicatorsEnabled(false)
        built.isLoggingEnabled = true
        Picasso.setSingletonInstance(built)
    }


}