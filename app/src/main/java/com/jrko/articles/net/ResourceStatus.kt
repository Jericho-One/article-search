package com.jrko.articles.net

/**
 * Current status of a a resource that is being provided to the UI.
 *
 * Meant to be used in conjunction with Repository classes that return
 * LiveData<Resource<T>> to pass back the current loading status of the data
 * being loaded.
 */
enum class ResourceStatus {
    SUCCESS,
    ERROR,
    LOADING,
    IDLE
}