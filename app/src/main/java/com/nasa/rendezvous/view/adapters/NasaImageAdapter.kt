package com.nasa.rendezvous.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nasa.rendezvous.R
import com.nasa.rendezvous.model.NasaImages
import com.nasa.rendezvous.utils.ImageUtils
import kotlinx.android.synthetic.main.item_recyclerview_main_activity.view.*

class NasaImageAdapter(
    private var imageList: MutableList<NasaImages>?,
    listener: OnAdapterItemClick
) :
    RecyclerView.Adapter<NasaImageAdapter.ViewHolder>() {
    private val clickListener: OnAdapterItemClick = listener
    private val tag = javaClass.simpleName

    class ViewHolder(view: View, onAdapterItemClick: OnAdapterItemClick) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        var imageView: ImageView
        var title: TextView
        private var onAdapterItemClick: OnAdapterItemClick? = onAdapterItemClick

        init {
            super.itemView
            itemView.setOnClickListener(this)
            imageView = view.item_recyclerViewMain_imageView
            title = view.item_recyclerViewMain_tvTitle
        }

        override fun onClick(p0: View?) {
            onAdapterItemClick?.onItemClick(adapterPosition, this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_recyclerview_main_activity, parent, false
        )
        return ViewHolder(itemView, clickListener)
    }

    override fun getItemCount(): Int {
        return imageList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = imageList?.get(position)
        Log.d(tag, "image Url: ${list?.url}")
        holder.title.text = list?.title
        list?.url?.let {
            ImageUtils.showImage(
                it,
                holder.imageView,
                holder.imageView.context.javaClass.simpleName
            )
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
        Log.d(tag, "new list size: ${newImageList.size}")
    }

    fun getData(): MutableList<NasaImages>? {
        return imageList
    }

    interface OnAdapterItemClick {
        fun onItemClick(position: Int, viewHolder: ViewHolder)
    }

}