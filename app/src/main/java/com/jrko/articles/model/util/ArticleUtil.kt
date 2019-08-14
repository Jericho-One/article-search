package com.jrko.articles.model.util

import com.jrko.articles.model.Doc

object ArticleUtil {

    /**
     *
     * Gets the headline text for the view. If actual headline print value or value is null
     * take the abstract (this might be making an assumption, TODO investigate this assumption
     *
     * **/
    fun getHeadlineText(doc: Doc): CharSequence? {
        var text: CharSequence? = null
        doc.headline.apply {
            if (this.print_headline != null) {
                text = this.print_headline
            } else if (this.main != null) {
                text = this.main
            }
        }
        if (text == null) {
            text = doc.abstract
        }
        return text
    }

}