package com.jrko.articles.net

data class Resource<T>(var status: ResourceStatus, var data: T?, var message: String?)