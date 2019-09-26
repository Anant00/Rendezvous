package com.nasa.rendezvous.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nasa.rendezvous.application.App
import com.nasa.rendezvous.model.NasaImages
import com.nasa.rendezvous.repository.localrepository.LocalDatabaseRepository
import io.reactivex.Flowable

class DatabaseViewModel(application: Application) : AndroidViewModel(application) {
    var databaseRepository = LocalDatabaseRepository(application)

    fun insert(imageList: List<NasaImages>) {
        databaseRepository.insert(imageList.reversed())
    }

    fun delete() {
        databaseRepository.deleteAll()
    }

    fun checkIfDatabaseIsUpdated(): List<NasaImages> {
        return databaseRepository.checkIfDatabaseIsUpdate()
    }

    fun getDataFromDatabase(): Flowable<List<NasaImages>>? {
        return App.database?.imageDao()?.let {
            it.getAllImagesFromDatabase().switchMap { data ->
                Flowable.just(data)
            }
        }
    }
}