package com.nasa.rendezvous.model

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

// for sending whole list to next activity inside intent instead of fetching again from database or api
@Parcelize
data class NasaImages(
    @Expose
    @SerializedName("copyright")
    var copyright: String? = null,
    @SerializedName("date")
    @Expose
    @PrimaryKey
    var date: String,
    @SerializedName("explanation")
    @Expose
    var explanation: String? = null,
    @SerializedName("hdurl")
    @Expose
    var hdurl: String? = null,
    @SerializedName("media_type")
    @Expose
    var mediaType: String? = null,
    @SerializedName("title")
    @Expose
    var title: String? = null,
    @SerializedName("url")
    @Expose
    var url: String? = null
) : Parcelable {
    override fun toString(): String {
        return "Image(copy=$copyright, date=$date, explanation=$explanation, hdurl=$hdurl, mediaType=$mediaType," +
                " title=$title, url=$url)"
    }
}
