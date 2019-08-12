package com.jrko.articles.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jrko.articles.R
import com.jrko.articles.model.Doc

class ArticleDetailFragment(private val doc: Doc) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val result = inflater.inflate(R.layout.article_detail, container, false)
        bindViews(result)
        setHasOptionsMenu(true)
        return result
    }

    private fun bindViews(result: View) {
        val textView: TextView = result.findViewById(R.id.headline)
        textView.text = doc.headline.print_headline
        val paragraph: TextView = result.findViewById(R.id.lead_paragraph)
        paragraph.text = doc.lead_paragraph
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.article_details_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.share -> {
                //TODO make this sharing more purdy
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, doc.abstract)
                    type = "text/plain"
                }
                startActivity(sendIntent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}