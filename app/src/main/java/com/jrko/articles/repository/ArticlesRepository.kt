package com.jrko.articles.repository

import com.jrko.articles.model.ArticleResponse
import com.jrko.articles.net.GetDataService
import com.jrko.articles.net.RetrofitInstance
import retrofit2.Response
import javax.inject.Singleton

@Singleton
class ArticlesRepository {
    private val client = RetrofitInstance().getRetrofitInstance()?.create(GetDataService::class.java)

    suspend fun getArticles(searchQuery: String?, pageCount: Int) : Response<ArticleResponse>? {
        return client?.getArticles(searchQuery, pageCount)
    }
}