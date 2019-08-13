package com.jrko.articles.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.jrko.articles.R
import com.jrko.articles.fragment.ArticleDetailFragment
import com.jrko.articles.fragment.ArticlesListFragment
import com.jrko.articles.model.Doc
import com.jrko.articles.net.GetArticlesClient
import com.jrko.articles.net.NetworkConnectionLiveData
import com.jrko.articles.net.RetrofitInstance
import com.jrko.articles.repository.ArticlesRepository
import com.jrko.articles.viewmodel.ArticlesListViewModel
import com.jrko.articles.viewmodel.ArticlesListViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ArticlesListFragment.Callback {

    private lateinit var networkConnection: NetworkConnectionLiveData

    //TODO use dagger?? If this was a larger project, it would be a good idea, but for brevity...
    private var articlesRepository: ArticlesRepository = ArticlesRepository(RetrofitInstance().getRetrofitInstance()?.create(GetArticlesClient::class.java))
    private lateinit var articlesListViewModel: ArticlesListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        monitorNetworkConnections()
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        if (savedInstanceState == null) {
            initFragment(articlesListViewModel)
        }
    }

    private fun initViewModel() {
        val viewModelFactory = ArticlesListViewModelFactory(articlesRepository)
        articlesListViewModel = ViewModelProviders.of(this, viewModelFactory).get(ArticlesListViewModel::class.java)
    }

    private fun monitorNetworkConnections() {
        networkConnection = NetworkConnectionLiveData(applicationContext)
        val networkObserver = Observer<Boolean> { connected ->
            if (!connected) {
                articlesListViewModel.cancelAllRequests()
                Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show()
            } else {
                articlesListViewModel.resumeNetworkRequests()
            }
        }
        networkConnection.observe(this, networkObserver)
    }

    private fun initFragment(viewModel: ArticlesListViewModel) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, ArticlesListFragment(viewModel))
            .commit()
    }

    override fun openArticle(doc: Doc?) {
        doc?.apply {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, ArticleDetailFragment(this))
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val articlesFrag = supportFragmentManager.findFragmentByTag(ArticlesListFragment.TAG)
        if (articlesFrag != null && articlesFrag.isVisible) {
            articlesListViewModel.clearSearchQuery()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        val sv: SearchView? = menu.findItem(R.id.search).actionView as SearchView
        sv?.apply {
            this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(text: String?): Boolean {
                    return true
                }

                override fun onQueryTextSubmit(text: String?): Boolean {
                    articlesListViewModel.getArticles(text)
                    return true
                }
            })
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.refresh -> {
                //TODO call fragment to refresh
                Toast.makeText(this, "Hay there", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
