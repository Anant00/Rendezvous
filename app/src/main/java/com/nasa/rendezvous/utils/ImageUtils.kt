package com.nasa.rendezvous.utils

import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

object ImageUtils {

    fun showImage(url: String, imageView: ImageView) {
        Picasso.get().load(url).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, object : Callback {
            override fun onSuccess() {

            }

            override fun onError(e: Exception?) {
                Picasso.get().load(url).into(imageView)
            }
        })
    }
}