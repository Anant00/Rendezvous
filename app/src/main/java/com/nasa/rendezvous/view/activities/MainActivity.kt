package com.nasa.rendezvous.view.activities

import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppTheme.setupInsets(toolbar, recyclerView, parent_main_activity, this)
        progressBar.visibility = VISIBLE
        getDates()
        setUpViewModelMain()
        setRecyclerView()
        getData(startDate, "2019-09-24")
    }

    private fun getDates() {
        // fetching previous 15+1 days data.
        startDate = DateRangeUtils.getMonthBackDate(15)
        endDate = DateRangeUtils.getTodayDate()
    }

    private fun setUpViewModelMain() {
        viewModel = ViewModelProviders.of(this).get(ViewModelMain::class.java)
    }

    private fun getData(startDate: String, endDate: String) {
        viewModel.getRxNasaImages(startDate, endDate)?.observe(this, Observer { imageList ->
            if (imageList != null) {
                images = mutableListOf()
                println(imageList.size)
                for (it in imageList) {
                    if (it.mediaType != "video") {
                        images.add(it)
                        Log.d(tag, "image data: ${it.url}")
                        nasaImageAdapter = NasaImageAdapter(images, this)
                        recyclerView.adapter = nasaImageAdapter
                    }
                }
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
        recyclerView.setItemViewCacheSize(50)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearDisposable()
    }

}
