package com.nasa.rendezvous.api.apiservice

import com.nasa.rendezvous.model.NasaImages
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/* @param [start_date] is the date from which the api will start fetching the data
    * and @param [end_date] is the last (the latest date) to which data will be fetched.
    *
    * [end_date] should not be greater than Today's date
    * Nasa Api refers USA date. So be careful while passing local date as [end_date] instead of usa date
    * It will return error only  if app is running after  12 in the night.
    */
interface ApiService {

    /*
     *Call this method when using coroutines... it is a suspend fun.
     *TODO : put api key in build config for release mode.
     */
    @GET("apod?api_key=BQWp2dHaRytHEV7UtZv1t9vOvm9N03u7aXQ8Cgn7")
    suspend fun getImages(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<List<NasaImages>>

    //Call this method when using RxJava.
    @GET("apod?api_key=BQWp2dHaRytHEV7UtZv1t9vOvm9N03u7aXQ8Cgn7")
    fun getImagesRx(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Observable<Response<List<NasaImages>>>
}