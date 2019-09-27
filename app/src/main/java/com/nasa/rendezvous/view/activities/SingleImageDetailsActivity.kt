package com.nasa.rendezvous.view.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nasa.rendezvous.R
import com.nasa.rendezvous.model.NasaImages
import com.nasa.rendezvous.utils.AppTheme
import com.nasa.rendezvous.utils.ImageUtils
import com.nasa.rendezvous.utils.IntentUtil
import com.nasa.rendezvous.view.adapters.SingleImageAdapter
import com.squareup.leakcanary.RefWatcher
import kotlinx.android.synthetic.main.singleimagedetail_activity.*


class SingleImageDetailsActivity : AppCompatActivity() {

    private val tag = javaClass.simpleName
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var position = 0
    private lateinit var singleImageAdapter: SingleImageAdapter
    private var refWatcher: RefWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.singleimagedetail_activity)

        AppTheme.setupInsets(toolbar, null, parent_constraint_singleImageDetail_layout, this)
        getData()
        setViewPagerAdapter()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
    }

    private fun getData(): List<NasaImages>? {
        // getting data from previous activity. It is better to pass whole list rather than  fetching again even from local database
        val item = intent.getParcelableExtra<IntentUtil>("list")
        position = intent.getIntExtra("position", 0)
        Log.d(tag, "items ${item?.imageList?.size}")
        return item?.imageList
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
                return true
            }
            R.id.action_details -> {
                Log.d(tag, "action details clicked")
                showDetails()
                return true
            }
        }
        return true
    }

    private fun setViewPagerAdapter() {
        singleImageAdapter = SingleImageAdapter(this, getData()!!)
        viewPager.adapter = singleImageAdapter
        viewPager.setCurrentItem(position, true)
    }

    @SuppressLint("InflateParams")
    private fun showDetails() {
        bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView =
            layoutInflater.inflate(R.layout.details_bottom_sheet, null)
        bottomSheetDialog.setContentView(bottomSheetView)
        (bottomSheetView.parent as View).setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                android.R.color.transparent
            )
        )
        val position = viewPager.currentItem
        Log.d(tag, "title - ${getData()!![position].mediaType}")
        val btmImageView: ImageView = bottomSheetView.findViewById(R.id.btmSheetImageView)
        val btmTitle: TextView = bottomSheetView.findViewById(R.id.tvBtmSheetView_title)
        val btmDescription: TextView = bottomSheetView.findViewById(R.id.tvBtmSheetView_description)
        val btmSheetCopyRight: TextView = bottomSheetView.findViewById(R.id.tvBtmSheetView_copyright)
        val btmSheetDate: TextView = bottomSheetView.findViewById(R.id.tvBtmSheetView_date)
        BottomSheetBehavior.from(bottomSheetView.parent as View).peekHeight = 500

        btmTitle.text = getData()!![position].title
        btmDescription.text = getData()!![position].explanation
        btmSheetDate.text = getData()!![position].date
        try {
            btmSheetCopyRight.text = getData()!![position].copyright
        } catch (e: Exception) {
            Log.e(tag, "BottomSheetCopyRight ${e.localizedMessage}")
            btmSheetCopyRight.visibility = View.GONE
        }
        getData()?.get(position)?.url?.let {
            ImageUtils.showImage(it, btmImageView)
        }
        bottomSheetDialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAfterTransition()
    }

    override fun onDestroy() {
        super.onDestroy()
        refWatcher?.watch(this)
        try {
            bottomSheetDialog.dismiss()
        } catch (e: Exception) {
            Log.e(tag, "bottomSheet ex. ${e.localizedMessage}")
        }
    }
}