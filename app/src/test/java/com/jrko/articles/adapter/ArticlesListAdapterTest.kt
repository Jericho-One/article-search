package com.jrko.articles.adapter

import android.content.Context
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jrko.articles.model.*
import com.jrko.articles.repository.ArticlesRepository
import com.jrko.articles.viewmodel.ArticlesListViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class ArticlesListAdapterTest {
    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var adapter: ArticlesListAdapter
    private lateinit var articlesListViewModel: ArticlesListViewModel

    private val articlesRepository: ArticlesRepository = mock(ArticlesRepository::class.java)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        articlesListViewModel = ArticlesListViewModel(articlesRepository, mock(Context::class.java))
        adapter = ArticlesListAdapter(articlesListViewModel, object : RecyclerViewListener{
            override fun onClick(view: View?, position: Int) {
                //Do nothing for test
            }

        })
    }

    @Test
    fun adapterPaginationLogic() {
        //TODO this test data should live somewhere else
        val docsList = listOf(Doc("id1",
            "Some words",
            Blog(),
            Byline("jrko", "one", emptyList()),
            "blurb",
            Headline("You won't believe this",
                "but you will",
                null,
                "headline",
                "You really will believe this",
                "lol",
                "lol"),
            emptyList(),
            "Some more words",
            emptyList(),
            "no",
            "page",
            "date",
            "section name",
            "snippet",
            "source",
            "subsection name",
            "material",
            "uri",
            "url",
            50))
        var articleResponse = ArticleResponse("foo",
            Response(docsList, Meta(15, 0, 42)),
                "success")
        adapter.data = articleResponse
        // the adapter will return a count one greater than docs size
        // if the hits in meta are greater than total items.
        // this is how it determines to show the progress spinner
        // at the bottom of the paginated list
        assert(adapter.itemCount == articleResponse.response.docs.size + 1)

        //change hits to match data size
        articleResponse = ArticleResponse("foo",
            Response(docsList, Meta(1, 0, 42)),
            "success")
        adapter.data = articleResponse

        assert(adapter.itemCount == articleResponse.response.docs.size)
    }
}