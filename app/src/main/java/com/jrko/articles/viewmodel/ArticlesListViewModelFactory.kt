package com.jrko.articles.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jrko.articles.repository.ArticlesRepository

class ArticlesListViewModelFactory(private val repository: ArticlesRepository, private val applicationContext: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ArticlesListViewModel(repository, applicationContext) as T
    }
}