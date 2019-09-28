package com.nasa.rendezvous.view.adapters

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.nasa.rendezvous.R
import com.nasa.rendezvous.model.NasaImages
import com.nasa.rendezvous.utils.ImageUtils
import com.nasa.rendezvous.utils.IntentUtil
import com.nasa.rendezvous.view.activities.SingleImageDetailsActivity
import kotlinx.android.synthetic.main.item_recyclerview_main_activity.view.*


class NasaImageAdapter(private var imageList: MutableList<NasaImages>?, private val activity: Activity) :
    RecyclerView.Adapter<NasaImageAdapter.ViewHolder>() {
    private val tag = javaClass.simpleName
    private lateinit var intentUtil: IntentUtil

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.item_recyclerViewMain_imageView
        var title: TextView = view.item_recyclerViewMain_tvTitle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_recyclerview_main_activity, parent, false
        )
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return imageList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(tag, "image Url: ${imageList?.get(position)?.url}")
        if (imageList?.get(position)?.mediaType == "image") {
            imageList!![position].url?.let {
                if (!imageList!![position].title.equals("")) {
                    holder.title.text = imageList!![position].title
                    ImageUtils.showImage(it, holder.imageView, activity.javaClass.simpleName)
                    intentUtil = IntentUtil(imageList!!)
                    Log.d(tag, "adapter image Url: ${imageList!![position].url}")
                }
            }
        }
        ImageUtils.setImageRatio(holder.imageView)
        holder.itemView.setOnClickListener {
            onItemClick(holder)
        }
    }

    fun addData(newImageList: List<NasaImages>) {
        try {
            imageList!!.clear()
        } catch (e: Exception) {
            Log.e(tag, e.printStackTrace().toString())
        }
        imageList?.addAll(newImageList)

        notifyItemInserted(imageList!!.size - 1)
        notifyDataSetChanged()
        Log.d(tag, "new list size: ${newImageList.size}")
    }

    private fun onItemClick(holder: ViewHolder) {

        val intent = Intent(activity.applicationContext, SingleImageDetailsActivity::class.java)
        intent.putExtra("list", intentUtil)
        intent.putExtra("position", holder.adapterPosition)
        val options = ActivityOptionsCompat.makeScaleUpAnimation(
            holder.imageView, 0, 0, holder.imageView.width, holder.imageView.height
        ).toBundle()
        activity.startActivity(intent, options)
    }
}