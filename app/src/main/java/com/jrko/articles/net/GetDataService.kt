package com.jrko.articles.net

import com.jrko.articles.model.ArticleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GetDataService {
    @GET("articlesearch.json/")
    suspend fun getArticles(@Query(value = "q") query: String?,
                            @Query(value = "page") int: Int) : Response<ArticleResponse>
}