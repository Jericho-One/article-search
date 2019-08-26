package com.jrko.articles.viewmodel

import android.content.Context
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
        val mockContext =  mock(Context::class.java)
        articlesListViewModel = ArticlesListViewModel(articlesRepository, mockContext)
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


        // test loading more pages based on same inquiry
        articlesListViewModel.getArticles("word")
        liveData.observeOnce {
            assert(it.status == ResourceStatus.LOADING)
        }

        // test clearing doesn't fire off more requests
        articlesListViewModel.clearSearchQuery()
        liveData.observeOnce {
            assert(it.status == ResourceStatus.IDLE)
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
