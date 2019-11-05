package com.jrko.articles.repository

import com.jrko.articles.model.ArticleResponse
import com.jrko.articles.net.GetArticlesClient
import retrofit2.Response
import javax.inject.Singleton

//TODO use dagger for DI?? For a larger project, yes...
@Singleton
class ArticlesRepository(private val client: GetArticlesClient?) {
    suspend fun getArticles(searchQuery: String?, pageCount: Int) : Response<ArticleResponse>? {
        return client?.getArticles(searchQuery, pageCount)
    }
}