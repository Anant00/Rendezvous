package com.nasa.rendezvous.utils

import android.content.res.Resources
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

object ImageUtils {

    fun showImage(url: String, imageView: ImageView, tag: String) {
        Picasso.get().load(url).networkPolicy(NetworkPolicy.OFFLINE).tag(tag).into(imageView, object : Callback {
            override fun onSuccess() {

            }

            override fun onError(e: Exception?) {
                Picasso.get().load(url).tag(tag).into(imageView)
            }
        })
    }

    fun setImageRatio(imageView: ImageView) {
        val height: Int = Resources.getSystem().displayMetrics.heightPixels
        val layoutParams: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(imageView.layoutParams)
        layoutParams.height = (height * 25) / 100
        imageView.layoutParams = layoutParams
    }

    fun cancelPicasso(tag: String) {
        Picasso.get().cancelTag(tag)
    }
}