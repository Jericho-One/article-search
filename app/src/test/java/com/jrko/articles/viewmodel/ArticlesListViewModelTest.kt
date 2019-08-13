package com.jrko.articles.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import com.jrko.articles.model.ArticleResponse
import com.jrko.articles.model.Meta
import com.jrko.articles.net.ResourceStatus
import com.jrko.articles.repository.ArticlesRepository
import com.jrko.articles.util.OneTimeObserver
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.stub
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import retrofit2.Response

class ArticlesListViewModelTest {
    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var articlesListViewModel: ArticlesListViewModel

    private val articlesRepository: ArticlesRepository = mock(ArticlesRepository::class.java)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        articlesListViewModel = ArticlesListViewModel(articlesRepository)
    }

    @Test
    fun getArticles() {
        val response = Response.success(
            ArticleResponse(
                "foo",
                com.jrko.articles.model.Response(emptyList(),
                    Meta(15, 0, 23)),
                "Ok"
            )
        )

        articlesRepository.stub {
            onBlocking {
                getArticles("word", 0)
            }.doReturn(response)
        }

        val liveData = articlesListViewModel.getListResource()
        liveData.observeOnce {
            assert(it.status == ResourceStatus.IDLE)
        }

        articlesListViewModel.requestList()
        liveData.observeOnce {
            assert(it.status == ResourceStatus.LOADING)
        }

    }

    @Test
    fun clearSearchQuery() {
        //TODO
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
}

fun <T> LiveData<T>.observeOnce(onChangeHandler: (T) -> Unit) {
    val observer = OneTimeObserver(handler = onChangeHandler)
    observe(observer, observer)
}
