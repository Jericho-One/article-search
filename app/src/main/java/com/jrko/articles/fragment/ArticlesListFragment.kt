package com.jrko.articles.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.jrko.articles.R
import com.jrko.articles.adapter.ArticlesListAdapter
import com.jrko.articles.adapter.RecyclerViewListener
import com.jrko.articles.model.ArticleResponse
import com.jrko.articles.model.Doc
import com.jrko.articles.net.Resource
import com.jrko.articles.net.ResourceStatus
import com.jrko.articles.viewmodel.ArticlesListViewModel


class ArticlesListFragment(/*use dagger for this*/viewModel: ArticlesListViewModel) : Fragment() {
    private var articlesListAdapter: ArticlesListAdapter
    private var articlesListViewModel = viewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateViewGroup: Group
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var callback: Callback

    init {
        val listClickListener = object : RecyclerViewListener {
            override fun onClick(view: View?, position: Int) {
                callback.openArticle(articlesListViewModel.getArticle(position))
            }
        }
        articlesListAdapter = ArticlesListAdapter(viewModel, listClickListener)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = activity as Callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val result = inflater.inflate(R.layout.articles_list, container, false)
        emptyStateViewGroup = result.findViewById(R.id.empty_state_group)
        swipeRefreshLayout = result.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout.setOnRefreshListener { refreshListWithCurrentSearchQuery() }
        recyclerView = result.findViewById(R.id.list_recycler_view)
        //if the items in the adapter don't change the size of the recycler view, optimize
        //rendering by setting fixed size to true
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = articlesListAdapter
        return result
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val listObserver = Observer<Resource<ArticleResponse>?> {
            it?.apply {
                when (this.status) {
                    ResourceStatus.IDLE -> {
                        handleIdleState(this.data)
                    }
                    ResourceStatus.LOADING -> {
                        handleLoadingState(this.data)
                    }
                    ResourceStatus.ERROR -> {
                        handleErrorState(this)
                    }
                    ResourceStatus.SUCCESS -> {
                        handleSuccessState(this.data)
                    }
                }
            }
        }
        articlesListViewModel.getListResource().observe(this, listObserver)
    }

    private fun handleIdleState(data: ArticleResponse?) {
        hideRefresh()
        if (data == null) {
            articlesListAdapter.setList(null)
            emptyStateViewGroup.visibility = View.VISIBLE
        }
    }

    private fun handleLoadingState(data: ArticleResponse?) {
        if (data == null) {
            showRefresh()
            articlesListAdapter.setList(null)
        } else {
            hideRefresh()
        }
        emptyStateViewGroup.visibility = View.GONE
    }

    private fun handleErrorState(resource: Resource<ArticleResponse>) {
        hideRefresh()
        resource.message?.let { message ->
            view?.let { currentView ->
                Snackbar.make(currentView, message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun handleSuccessState(data: ArticleResponse?) {
        hideRefresh()
        articlesListAdapter.setList(data)
        emptyStateViewGroup.visibility =
            if (articlesListAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun showRefresh() {
        swipeRefreshLayout.isRefreshing = true
    }

    private fun hideRefresh() {
        swipeRefreshLayout.isRefreshing = false
    }

    private fun refreshListWithCurrentSearchQuery() {
        articlesListViewModel.refreshList()
    }

    interface Callback {
        fun openArticle(doc: Doc?)
    }

    companion object {
        const val TAG = "ArticlesListFrag"
    }
}