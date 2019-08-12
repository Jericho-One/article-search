package com.jrko.articles.model

data class ArticleResponse(
    val copyright: String,
    val response: Response,
    val status: String
)

data class Response(
    var docs: List<Doc>,
    val meta: Meta
)

data class Doc(
    val id: String,
    val abstract: String,
    val blog: Blog,
    val byline: Byline,
    val document_type: String,
    val headline: Headline,
    val keywords: List<Keyword>,
    val lead_paragraph: String,
    val multimedia: List<Multimedia>,
    val news_desk: String,
    val print_page: String,
    val pub_date: String,
    val section_name: String,
    val snippet: String,
    val source: String,
    val subsection_name: String,
    val type_of_material: String,
    val uri: String,
    val web_url: String,
    val word_count: Int
)

data class Byline(
    val organization: String,
    val original: String,
    val person: List<Person>
)

data class Person(
    val firstname: String,
    val lastname: String,
    val middlename: String,
    val organization: String,
    val qualifier: String,
    val rank: Int,
    val role: String,
    val title: String
)

class Blog(
)

data class Multimedia(
    val caption: Any,
    val credit: Any,
    val crop_name: String,
    val height: Int,
    val legacy: Legacy,
    val rank: Int,
    val subType: String,
    val subtype: String,
    val type: String,
    val url: String,
    val width: Int
)

class Legacy(
)

data class Headline(
    val content_kicker: Any,
    val kicker: Any,
    val main: String?,
    val name: Any,
    val print_headline: String?,
    val seo: Any,
    val sub: Any
)

data class Keyword(
    val major: String,
    val name: String,
    val rank: Int,
    val value: String
)

data class Meta(
    val hits: Int,
    val offset: Int,
    val time: Int
)