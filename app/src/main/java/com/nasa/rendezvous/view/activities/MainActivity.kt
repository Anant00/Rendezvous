package com.nasa.rendezvous.view.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nasa.rendezvous.R
import com.nasa.rendezvous.application.App
import com.nasa.rendezvous.model.NasaImages
import com.nasa.rendezvous.utils.AppTheme
import com.nasa.rendezvous.utils.DateRangeUtils
import com.nasa.rendezvous.view.adapters.NasaImageAdapter
import com.nasa.rendezvous.viewmodel.DatabaseViewModel
import com.nasa.rendezvous.viewmodel.ViewModelMain
import com.squareup.leakcanary.RefWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ViewModelMain
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var nasaImageAdapter: NasaImageAdapter
    private val tag = javaClass.simpleName
    private lateinit var startDate: String
    private var refWatcher: RefWatcher? = null
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var endDate: String
    private lateinit var images: MutableList<NasaImages>

    // onScrollLoadMore variables
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    private val visibleThreshold = 10
    var firstVisibleItem: Int = 0
    private var previousTotal = 0
    private var loading = true
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPrefEditor: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        refWatcher = App.getRefWatcher(this)
        AppTheme.setupInsets(toolbar, recyclerView, parent_main_activity, this)
        progressBar.visibility = VISIBLE
        setSharedPref()
        setUpViewModelMain()
        setRecyclerView()
        getDates()
        /* isDatabaseAvailable() checks whether the data is available from the database, if it is available, show the data.
         if not, fetch from the api store it the local database and then show it.
         Whether the internet is available or not, does not matter in out case because api should not be called again for already seen data
         even when the internet is available.

         NOTE: All the data will be stored in database no matter up to which page user scrolls. IT might result in large app cache size.

         */
        isDatabaseAvailable(startDate, endDate)
        onScrollLoadMoreData()

    }

    private fun setSharedPref() {
        sharedPreferences = getSharedPreferences(
            "latestDates",
            MODE_PRIVATE
        )
        sharedPrefEditor = sharedPreferences.edit()
        sharedPrefEditor.apply()
    }

    private fun getDates() {
        // fetching previous 15+1 days data.
        startDate = DateRangeUtils.getDaysBackDate(15)
        endDate = DateRangeUtils.getTodayDate()
        sharedPrefEditor.putString("last end date", endDate)
        sharedPrefEditor.apply()
    }

    private fun setUpViewModelMain() {
        viewModel = ViewModelProviders.of(this).get(ViewModelMain::class.java)
        databaseViewModel = ViewModelProviders.of(this).get(DatabaseViewModel(application)::class.java)
    }

    private fun getData(startDate: String, endDate: String) {
        viewModel.getNasaImages(startDate, endDate)?.observe(this, Observer { imageList ->
            if (imageList != null) {
                images = mutableListOf()
                progressBar.visibility = GONE
                println(imageList[0].url)
                for (images in imageList) {
                    if (images.mediaType != "video") {
                        this.images.add(images)
                    }
                }
                Log.d(tag, "inserted data size: ${images.size}")
                progressBar.visibility = GONE
                databaseViewModel.insert(this.images)
            }
        })
    }

    private fun setRecyclerView() {
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setItemViewCacheSize(50)
        nasaImageAdapter = NasaImageAdapter(arrayListOf(), this)
        recyclerView.adapter = nasaImageAdapter
    }

    private fun onScrollLoadMoreData() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                visibleItemCount = recyclerView.childCount
                totalItemCount = linearLayoutManager.itemCount
                firstVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    // get the new start data and new end date.
                    startDate = sharedPreferences.getString("start date", startDate)!!
                    endDate = sharedPreferences.getString("end date", endDate)!!
                    val (newStartDate, newEndDate) = DateRangeUtils.updateDate(startDate)
                    Log.d(tag, "newStartDate: $newStartDate , newEndDate $newEndDate")
                    loadMoreData(newStartDate, newEndDate)
                    loading = true
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            }
        })
    }

    private fun loadMoreData(startDate: String, endDate: String) {
        progressBar_loadMore.visibility = VISIBLE
        viewModel.getNasaImages(startDate, endDate)?.observe(this, Observer { imageList ->
            this.images = mutableListOf()
            for (images in imageList) {
                if (images.mediaType != "video") {
                    this.images.add(images)
                }
            }
            images
            databaseViewModel.insert(images)
            Log.d(tag, "success on load more ${images.size}")
            progressBar_loadMore.visibility = GONE
            // update dates to pass for next loadMoreData
            this.startDate = startDate
            this.endDate = endDate

            /* storing data in sharedPref so that when user recyclerView is at the bottom of screen, it should pass the date till the data
                has been fetched. Not required when fetching data just from api. But, when the app is killed, and if the app has data in local storage,
                using getDateToday() will return today's date, which in turn fetch the duplicate data and insert it at the bottom of the reyclerview.
                using sharedPref we are storing the date to which user has scrolled and saved the data, so that when next time user opens the app,
                it should fetch from that date and not today's date.
             */
            sharedPrefEditor = sharedPreferences.edit()
            sharedPrefEditor.putString("start date", startDate)
            sharedPrefEditor.putString("end date", endDate)
            sharedPrefEditor.apply()
        })
    }

    private fun isDatabaseAvailable(startDate: String, endDate: String) {
        databaseViewModel.getDataFromDatabase()?.let {
            databaseViewModel.getDataFromDatabase()!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { list ->
                        if (!list.isNullOrEmpty()) {
                            Log.d(tag, "data is not null, fetching from database ${list.size}")

                            if (shouldUpdateData()) {
                                val lastEndDate = sharedPreferences.getString("last end date", endDate)
                                Log.d(tag, "updating $lastEndDate")
                                getData(lastEndDate!!, DateRangeUtils.getTodayDate())
                                sharedPrefEditor.putString("last end date", DateRangeUtils.getTodayDate())
                                sharedPrefEditor.apply()
                            }
                            /* this is the only place where the data should be sent recyclerView adapter. getDataFromDatabase() returns flowable
                               which gets updated whenever there is a change in local database.
                               Calling it again wherever the data is inserted in local storage to update the recyclerView will result performance issue.
                             */
                            nasaImageAdapter.addData(list)
                            progressBar.visibility = GONE

                        } else {
                            Log.d(tag, "database is null, fetching from api and saving it")
                            getData(startDate, endDate)
                            Log.d(tag, "onRead Success: ${list.size}")
                        }
                        Log.d(tag, "from database----->\n$it")
                    },
                    { error ->
                        Log.e(tag, " $error.message")
                    }
                )
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        refWatcher?.watch(this)
        viewModel.clearDisposable()
    }


    /* this method checks whether the database should be updated  and fetch new data.
        It is used in the case where a user opens app after some days. In these cases, the database should  be updated from last end date to current date.
     */
    private fun shouldUpdateData(): Boolean {
        var isUpdateNeeded = false
        val lastEndDate = sharedPreferences.getString("last end date", endDate)
        Log.d(tag, "ïnside should $lastEndDate")
        if (lastEndDate != DateRangeUtils.getTodayDate()) {
            isUpdateNeeded = true
        }
        Log.d(tag, "is update needed $lastEndDate $isUpdateNeeded")
        return isUpdateNeeded
    }


}
