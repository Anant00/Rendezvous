package com.nasa.rendezvous.repository.localrepository

import android.app.Application
import android.util.Log
import com.nasa.rendezvous.application.App
import com.nasa.rendezvous.model.NasaImages
import com.nasa.rendezvous.model.localdatabasemodel.ImagesDao
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LocalDatabaseRepository(var application: Application) {
    private val imageDatabase = App.database
    var imagesDao: ImagesDao = imageDatabase?.let {
        imageDatabase.imageDao()
    }!!
    private val tag = javaClass.simpleName
    internal var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var allImages: MutableList<NasaImages>

    fun insert(imageList: List<NasaImages>) {
        insertAllData(imageList)
    }

    fun checkIfDatabaseIsUpdate(): List<NasaImages> {
        return imagesDao.checkIfDataBaseUpdate()
    }

    fun deleteAll() {
        deleteAllData()
    }

    fun deleteCompositeDisposable() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private fun insertAllData(imageList: List<NasaImages>) {

        CoroutineScope(IO).launch {
            imagesDao.insertAll(imageList)
            withContext(Main) {
                Log.d(tag, "notes inserted: Size ${imageList.size}")
            }
        }
    }

    private fun deleteAllData() {
        CoroutineScope(IO).launch {
            imagesDao.deleteAll()
            withContext(Main) {
                Log.d(tag, "notes deleted: Size")
            }
        }
    }


    fun getDataFromDatabase(): Flowable<List<NasaImages>>? {
        return imagesDao.getAllImagesFromDatabase()
    }
}

