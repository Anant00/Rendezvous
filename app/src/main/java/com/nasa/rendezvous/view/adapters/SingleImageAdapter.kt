package com.nasa.rendezvous.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager.widget.PagerAdapter
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.nasa.rendezvous.R
import com.nasa.rendezvous.model.NasaImages
import com.nasa.rendezvous.utils.ImageUtils
import kotlinx.android.synthetic.main.item_single_image.view.*


class SingleImageAdapter(private var context: Context, private var imageList: List<NasaImages>) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return imageList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = layoutInflater.inflate(R.layout.item_single_image, container, false)
        val imageView = itemView.imageView_single

        imageList[position].url?.let {
            ImageUtils.showImage(it, imageView, context.javaClass.simpleName)
            imageView.setOnTouchListener(ImageMatrixTouchHandler(context))
            container.addView(itemView)
        }
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as FrameLayout)
    }

}