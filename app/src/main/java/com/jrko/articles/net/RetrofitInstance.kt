package com.jrko.articles.net

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {
    private var retrofit: Retrofit? = null
    //TODO this should probably live somewhere else, maybe the local.properties??
    private val apiKey = "yDWyvWhvHV1WG70In3tAfqY7SkjL7JOS"

    fun getRetrofitInstance(): Retrofit? {
        val authInterceptor = Interceptor { chain ->

            val newUrl = chain.request().url()
                .newBuilder()
                .addQueryParameter("api-key", apiKey)
                .build()

            val newRequest = chain.request()
                .newBuilder()
                .url(newUrl)
                .build()

            chain.proceed(newRequest)
        }

        val articlesClient = OkHttpClient().newBuilder()
            .addInterceptor(authInterceptor)
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .client(articlesClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }

    companion object {
        private const val BASE_URL = "https://api.nytimes.com/svc/search/v2/"
    }
}