package com.example.urlshortener.domain.model

sealed interface Resource<out T> {

    data class Success<T>(
        val data: T
    ) : Resource<T>

    data class Error(
        val type: ErrorType,
        val message: String
    ) : Resource<Nothing>
}

enum class ErrorType {
    VALIDATION,
    NETWORK,
    SERVER,
    UNKNOWN
}