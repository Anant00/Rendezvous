package com.nasa.rendezvous.api.retrofitbuilder

import android.util.Log
import com.nasa.rendezvous.api.apiservice.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitBuilder {

    private const val BASE_URL = "https://api.nasa.gov/planetary/"
    private val tag: String = javaClass.simpleName

    private var okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }

    val apiService: ApiService by lazy {
        Log.d(tag, "Api service called")
        retrofitBuilder.build()
            .create(ApiService::class.java)
    }

    fun apiServiceResponse(): ApiService {
        return apiService
    }
}