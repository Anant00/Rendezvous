package com.nasa.rendezvous.utils

import android.os.Parcelable
import com.nasa.rendezvous.model.NasaImages
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IntentUtil(
    var imageList: List<NasaImages>
) : Parcelable