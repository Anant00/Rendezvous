package com.nasa.rendezvous.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nasa.rendezvous.api.retrofitbuilder.RetrofitBuilder
import com.nasa.rendezvous.model.NasaImages
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

object Repository {
    var job: CompletableJob? = null
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val tag = javaClass.simpleName
    private var nasaRxImages: MutableLiveData<List<NasaImages>>? = null

    /* 2 methods, same goal. One is using coroutines and the other is using RxJava. Both works.
     */
    fun getImages(startDate: String, endDate: String): LiveData<List<NasaImages>> {
        job = Job()
        val service = RetrofitBuilder.apiServiceResponse()
        return object : LiveData<List<NasaImages>>() {
            override fun onActive() {
                super.onActive()
                job?.let { theJob ->
                    CoroutineScope(IO + theJob).launch {
                        val response = service.getImages(startDate, endDate)
                        val images = RetrofitBuilder.apiService.getImages(startDate, endDate)
                        withContext(Main) {
                            if (response.isSuccessful) {
                                Log.d(tag, "isResponse: success? ${images.headers()}")
                                Log.d(tag, "the job called")
                                value = images.body()
                                theJob.complete()
                            }
                            if (!response.isSuccessful) {
                                Log.d(tag, "error ${images.errorBody()}")
                            }
                        }
                    }
                }
            }
        }
    }

    fun cancerJob() {
        job?.cancel()
    }

    fun getRxImages(startDate: String, endDate: String): MutableLiveData<List<NasaImages>>? {
        nasaRxImages = MutableLiveData()
        compositeDisposable.add(
            RetrofitBuilder.apiService.getImagesRx(startDate, endDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ images ->
                    nasaRxImages?.value = images.body()
                },
                    { error ->
                        Log.d(tag, "failed " + error.message)
                    }
                )
        )
        return nasaRxImages
    }

    fun disposeCompositeDisposable() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }
}