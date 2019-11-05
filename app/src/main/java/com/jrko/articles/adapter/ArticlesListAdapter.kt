package com.jrko.articles.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jrko.articles.R
import com.jrko.articles.model.ArticleResponse
import com.jrko.articles.model.Doc
import com.jrko.articles.model.util.ArticleUtil.getHeadlineText
import com.squareup.picasso.Picasso

class ArticlesListAdapter(private val callback: ArticleListCallback,
                            private val recyclerViewListener: RecyclerViewListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: ArticleResponse? = null
    private var lastDisplayedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_NORMAL) {
            ArticleViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item, parent, false),
                recyclerViewListener
            )
        } else {
            LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.paginated_list_loading_footer,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        var count = 0
        data?.response?.let { response ->
            count = response.docs.size
            if (response.meta.hits > count) {
                count++
            }
        }
        return count
    }

    override fun getItemViewType(position: Int): Int {
        val currentDataSetSize: Int? = data?.response?.docs?.size
        currentDataSetSize?.apply {
            if (position == this) {
                return VIEW_TYPE_MORE
            }
        }
        return VIEW_TYPE_NORMAL
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArticleViewHolder) {
            data?.apply {
                val doc = this.response.docs[position]
                holder.titleView.text = getHeadlineText(doc)
                populateImageView(holder, doc)
            }
            setAnimation(holder.parentView, holder.adapterPosition)
        } else {
            callback.onLoadMore()
            (holder as LoadingViewHolder).progressBar.visibility = View.VISIBLE
        }
    }

    private fun populateImageView(holder: ArticleViewHolder, doc: Doc) {
        doc.multimedia?.apply {
            if (this.isNotEmpty()) {
                //TODO don't have to rely on a the first element
                this[0].url.apply {
                    //TODO error handling??? Is Picasso magic?
                    Picasso.with(holder.parentView.context)
                        .load("https://static01.nyt.com/$this")
                        .placeholder(R.drawable.ic_camera_alt_gray_24dp)
                        .fit()
                        .into(holder.thumbnail)
                }
            }
        }
    }

    /**
     * Apply an animation to slide in from the left
     */
    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastDisplayedPosition) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.slide_in_from_left)
            viewToAnimate.startAnimation(animation)
            lastDisplayedPosition = position
        }
    }

    fun setList(listData: ArticleResponse?) {
        data = listData
        if (data == null) {
            lastDisplayedPosition = -1
        }
        notifyDataSetChanged()
    }

    inner class ArticleViewHolder(itemView: View, private val listener: RecyclerViewListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var parentView = itemView
        var titleView: TextView = itemView.findViewById(R.id.title_text)
        var thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)

        init {
            parentView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            this.listener.onClick(view, adapterPosition)
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    interface ArticleListCallback{
        fun onLoadMore()
    }

    companion object {
        private const val VIEW_TYPE_NORMAL = 0
        private const val VIEW_TYPE_MORE = 1
        //TODO should probably implement paging library to handle pagination, but didn't now for time
        /*val ArticlesDiffCallback = object : DiffUtil.ItemCallback<ArticleResponse>() {
            override fun areItemsTheSame(oldItem: ArticleResponse, newItem: ArticleResponse): Boolean {
                return oldItem.response === newItem.response
            }

            override fun areContentsTheSame(oldItem: ArticleResponse, newItem: ArticleResponse): Boolean {
                return oldItem == newItem
            }
        }*/
    }
}