package com.nasa.rendezvous.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nasa.rendezvous.R
import com.nasa.rendezvous.utils.DateRangeUtils
import com.nasa.rendezvous.viewmode.ViewModelMain

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ViewModelMain
    private val tag = javaClass.simpleName
    private lateinit var startDate: String
    private lateinit var endDate: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getDates()
        setUpViewModelMain()
        getData(startDate, endDate)
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
                println(imageList.size)
                for (images in imageList) {
                    if (images.mediaType != "video") {
                        Log.d(tag, "image data: ${images.url}")
                    }
                }
            }

        })
    }
}
