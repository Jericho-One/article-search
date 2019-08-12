package com.jrko.articles.adapter

import android.view.View

interface RecyclerViewListener {
    fun onClick(view: View?, position: Int)
}