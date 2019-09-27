package com.nasa.rendezvous.repository.localrepository

import android.util.Log
import com.nasa.rendezvous.application.App
import com.nasa.rendezvous.model.NasaImages
import com.nasa.rendezvous.model.localdatabasemodel.ImagesDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LocalDatabaseRepository {
    private val imageDatabase = App.database
    private var imagesDao: ImagesDao = imageDatabase?.let {
        imageDatabase.imageDao()
    }!!
    private val tag = javaClass.simpleName

    fun insert(imageList: List<NasaImages>) {
        insertAllData(imageList)
    }

    fun deleteAll() {
        deleteAllData()
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
}

