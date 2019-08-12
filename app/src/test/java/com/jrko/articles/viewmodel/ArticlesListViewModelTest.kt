package com.jrko.articles.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jrko.articles.repository.ArticlesRepository
import org.junit.Before
import org.junit.Test

import org.junit.Rule
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class ArticlesListViewModelTest {
    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var articlesListViewModel: ArticlesListViewModel

    private val articlesRepository: ArticlesRepository = mock(ArticlesRepository::class.java)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
//        articlesListViewModel = ArticlesListViewModel(articlesRepository)
    }

    @Test
    fun getArticles() {

    }

    @Test
    fun getListResource() {
    }

    @Test
    fun requestList() {
    }

    @Test
    fun clearSearchQuery() {
    }

    @Test
    fun hasCurrentSearchQuery() {
    }

    @Test
    fun refreshList() {
    }

    @Test
    fun getArticle() {
    }

    @Test
    fun cancelAllRequests() {
    }

    @Test
    fun resumeNetworkRequests() {
    }
}