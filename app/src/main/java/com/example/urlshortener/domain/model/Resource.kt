package com.example.urlshortener.domain.model

sealed interface Resource<out T> {

    data class Success<T>(
        val data: T
    ) : Resource<T>

    data class Error(
        val type: ErrorType
    ) : Resource<Nothing>
}

enum class ErrorType {
    EMPTY_URL,
    INVALID_URL,
    NETWORK,
    SERVER,
    UNKNOWN
}