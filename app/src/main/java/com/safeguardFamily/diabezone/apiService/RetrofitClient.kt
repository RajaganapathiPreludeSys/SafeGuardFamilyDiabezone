package com.safeguardFamily.diabezone.apiService

import com.safeguardFamily.diabezone.AppApplication
import com.safeguardFamily.diabezone.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BaseUrl = "https://safeguardfamily.com/apis/"

    private val retrofitClient: Retrofit.Builder by lazy {

        val levelType = if (BuildConfig.BUILD_TYPE.contentEquals("debug"))
            Level.BODY else Level.NONE

        val logging = HttpLoggingInterceptor()
        logging.setLevel(levelType)

        val okhttpClient = OkHttpClient.Builder()
        okhttpClient.addInterceptor(ConnectivityInterceptor(AppApplication.applicationContext()))
        okhttpClient.addInterceptor(logging)
        okhttpClient.addInterceptor(
            Interceptor { chain ->
                val builder = chain.request().newBuilder()
                builder.header("X-Auth-Token", "sgsrager32524542afg3423")
                return@Interceptor chain.proceed(builder.build())
            }
        )

        Retrofit.Builder()
            .baseUrl(BaseUrl)
            .client(okhttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
    }

    val apiInterface: ApiInterface by lazy {
        retrofitClient
            .build()
            .create(ApiInterface::class.java)
    }
}