package com.virtual_market.virtualmarket.api

import com.virtual_market.planetshipmentapp.MyUils.BasicAuthInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitClient {


    //    const val MainServer = "http://122.169.115.179/PlanetAPI/Web/"
        const val MainServer = "http://planetfurniture.in/PlanetAPITest/Web/"
//        const val MainServer = "http://planetfurniture.in/PlanetAPITest/Web/"
//        const val MainServer = "http://planetfurniture.in/PlanetAPILive/Web/"
//    const val MainServer = "http://planetfurniture.in/PlanetAPI_test/Web/"
//    const val MainServer = "http://planetfurniture.in:8080/PlanetAPI_test/Web/"
    private val loggingInterceptor = HttpLoggingInterceptor()

    val retrofitClient: Retrofit.Builder by lazy {

        //if there is no instance available... create new one
        val httpClientBuilder = OkHttpClient.Builder().retryOnConnectionFailure(true)
        httpClientBuilder.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val requestBuilder: Request.Builder = chain.request().newBuilder()
                requestBuilder.header("Content-Type", "application/json")
                requestBuilder.header("Accept", "application/json")
                requestBuilder.header("X-API-KEY", "cw00ggcsw4co0g804gcggwo088g4kokgk88sso4s")
                return chain.proceed(requestBuilder.build())
            }
        })

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        httpClientBuilder.addInterceptor(BasicAuthInterceptor())
        httpClientBuilder.addInterceptor(loggingInterceptor)

        val httpClient: OkHttpClient = httpClientBuilder.build()

        Retrofit.Builder()
            .baseUrl(MainServer)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
    }

    val apiInterface: ApiInterface by lazy {
        retrofitClient
            .build()
            .create(ApiInterface::class.java)
    }


}