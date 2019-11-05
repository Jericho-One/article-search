package com.jrko.articles.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jrko.articles.model.ArticleResponse
import com.jrko.articles.model.Doc
import com.jrko.articles.net.NetworkConnectionLiveData
import com.jrko.articles.net.Resource
import com.jrko.articles.net.ResourceStatus
import com.jrko.articles.repository.ArticlesRepository
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.Response

//TODO use dagger for DI?? If it was a larger project...
class ArticlesListViewModel(private val repository: ArticlesRepository, applicationContext: Context) : ViewModel() {
    var networkConnection: NetworkConnectionLiveData
    private val articlesLiveData = MutableLiveData<Resource<ArticleResponse>>()
    private var articlesResponse: ArticleResponse? = null
    private var currentSearchQuery: String? = null
    private var pageCount = 0
    private var networkJob: Job? = null

    init {
        articlesLiveData.value = Resource(ResourceStatus.IDLE, null, null)
        networkConnection = NetworkConnectionLiveData(applicationContext)
    }

    fun getArticles(searchQuery: String?) {
        val newQuery = currentSearchQuery != searchQuery
        if (newQuery) {
            currentSearchQuery = searchQuery
            articlesResponse = null
        }
        if (searchQuery != null) {
            networkJob?.cancel()
            articlesLiveData.value = Resource(ResourceStatus.LOADING, articlesResponse, null)
            //TODO this should live at the repository level instead of in the view model
            networkJob = CoroutineScope(Dispatchers.IO).async {
                val response = repository.getArticles(currentSearchQuery, pageCount)
                withContext(Dispatchers.Main) {
                    try {
                        if (response?.isSuccessful == true) {
                            handleSuccessCase(response)
                        } else {
                            articlesLiveData.value = Resource(
                                ResourceStatus.ERROR,
                                articlesResponse,
                                response?.message()
                            )
                        }
                    } catch (e: HttpException) {
                        articlesLiveData.value =
                            Resource(ResourceStatus.ERROR, articlesResponse, e.message())
                    } catch (e: Throwable) {
                        articlesLiveData.value =
                            Resource(ResourceStatus.ERROR, articlesResponse, e.message)
                    }
                }
            }
        } else {
            // notify observer that there is nothing happening
            articlesLiveData.value = Resource(ResourceStatus.IDLE, articlesResponse, null)
        }
    }

    private fun handleSuccessCase(response: Response<ArticleResponse>) {
        if (articlesResponse == null) {
            articlesResponse = response.body()
        } else {
            articlesResponse?.let {
                articlesResponse = appendToList(it, response.body())
            }
        }
        pageCount++
        articlesLiveData.value = Resource(ResourceStatus.SUCCESS, articlesResponse, null)
    }

    private fun appendToList(articlesResponse: ArticleResponse, body: ArticleResponse?) : ArticleResponse {
        //TODO this probably isn't the only thing to change, but for brevity
        body?.response?.docs?.apply {
            val newList: List<Doc> = articlesResponse.response.docs + this
            articlesResponse.response.docs = newList
        }
        return articlesResponse
    }

    fun getListResource(): LiveData<Resource<ArticleResponse>> {
        return articlesLiveData
    }

    fun requestList() {
        getArticles(currentSearchQuery)
    }

    fun clearSearchQuery() {
        currentSearchQuery = null
        articlesResponse = null
        articlesLiveData.value = Resource(ResourceStatus.IDLE, null, null)
    }

    fun refreshList() {
        articlesResponse = null
        pageCount = 0
        currentSearchQuery = null
        requestList()
    }

    fun getArticle(position: Int): Doc? {
        return articlesResponse?.response?.docs?.get(position)
    }

    fun cancelAllRequests() {
        networkJob?.cancel("Network Lost")
    }

    fun resumeNetworkRequests() {
        //TODO manage reconnection event?? Nothing to do at this point
    }

    fun getSearchQuery(): String? {
        return currentSearchQuery
    }
}