package com.nasa.rendezvous.view.activities

import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.nasa.rendezvous.R
import com.nasa.rendezvous.model.NasaImages
import com.nasa.rendezvous.utils.AppTheme
import com.nasa.rendezvous.utils.DateRangeUtils
import com.nasa.rendezvous.view.adapters.NasaImageAdapter
import com.nasa.rendezvous.viewmode.ViewModelMain
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ViewModelMain
    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    private lateinit var nasaImageAdapter: NasaImageAdapter
    private val tag = javaClass.simpleName
    private lateinit var startDate: String
    private lateinit var endDate: String
    private lateinit var images: MutableList<NasaImages>

    // onScrollLoadMore variables
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    private val visibleThreshold = 10
    var firstVisibleItem: Int = 0
    private var previousTotal = 0
    private var loading = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppTheme.setupInsets(toolbar, recyclerView, parent_main_activity, this)
        progressBar.visibility = VISIBLE
        getDates()
        setUpViewModelMain()
        setRecyclerView()
        getData(startDate, endDate)
        onScrollLoadMoreData()
    }

    private fun getDates() {
        // fetching previous 15+1 days data.
        startDate = DateRangeUtils.getDaysBackDate(15)
        endDate = DateRangeUtils.getTodayDate()
    }

    private fun setUpViewModelMain() {
        viewModel = ViewModelProviders.of(this).get(ViewModelMain::class.java)
    }

    private fun getData(startDate: String, endDate: String) {
        viewModel.getNasaImages(startDate, endDate)?.observe(this, Observer { imageList ->
            if (imageList != null) {
                images = mutableListOf()
                progressBar.visibility = GONE
                println(imageList[0].url)
                for (images in imageList) {
                    if (images.mediaType != "vi" +
                        "deo"
                    ) {
                        this.images.add(images)

                    }
                }
                nasaImageAdapter.addData(this.images)
                progressBar.visibility = GONE

            }
        })
    }

    private fun setRecyclerView() {
        staggeredGridLayoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        recyclerView.layoutManager = staggeredGridLayoutManager
        recyclerView.setItemViewCacheSize(20)
        nasaImageAdapter = NasaImageAdapter(arrayListOf(), this)
        recyclerView.adapter = nasaImageAdapter
    }

    private fun onScrollLoadMoreData() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                visibleItemCount = recyclerView.childCount
                totalItemCount = staggeredGridLayoutManager.itemCount
                firstVisibleItem = staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(null)[0]
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    // get the new start data and new end date
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
            // pass the newly fetched data to adapter
            nasaImageAdapter.addData(this.images)
            Log.d(tag, "success on load more ${images.size}")
            progressBar_loadMore.visibility = GONE

            // update dates to pass for next loadMoreData
            this.startDate = startDate
            this.endDate = endDate

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearDisposable()
    }

}
