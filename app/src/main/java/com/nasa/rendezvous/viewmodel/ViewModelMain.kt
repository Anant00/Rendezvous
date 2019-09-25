package com.nasa.rendezvous.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nasa.rendezvous.model.NasaImages
import com.nasa.rendezvous.repository.Repository

class ViewModelMain : ViewModel() {

    //private val _currentDate: MutableLiveData<String> = MutableLiveData()
    private var nasaImages: LiveData<List<NasaImages>>? = null
    // private var nasaRxImages: LiveData<List<NasaImages>>? = null
    // private val tag = javaClass.simpleName

    /* this method is used when called using coroutines.

    fun getLiveDateImages(startDate: String, endDate: String): LiveData<List<NasaImages>> {
        return Transformations
            .switchMap(_currentDate){
                Repository.getImages(startDate, endDate)
            }
    }
    */

    fun getNasaImages(startDate: String, endDate: String): LiveData<List<NasaImages>>? {
        // Not checking for null. if we do, this will return old data even when we call it in loadMore data and fetch new data.
        nasaImages = MutableLiveData()
        nasaImages = Repository.getRxImages(startDate, endDate)
        return nasaImages
    }

    /* This is used with coroutines. call this from mainActivity to avoid setting data in list again if it already has from previous execution.
        This is just like checking for null is Rxjava. If null then fetch new data else pass the old fetched data.
     */
    /*
    fun setCurrentDate(currentDate: String){
        if(_currentDate.value == currentDate){
            Log.d(tag, "method not called")
            return
        }
        Log.d(tag, "method called")
        _currentDate.value = currentDate
    }
    */

    // canecel job. Used with coroutines. Just like  rx disposable
    /*
    fun cancelJobs(){
        Repository.cancerJob()
    }
    */
    fun clearDisposable() {
        Repository.disposeCompositeDisposable()
    }
}