package com.example.urlshortener.presentation.urlshortener

import com.example.urlshortener.domain.model.ShortenedUrl

data class UrlShortenerState(
    val urlInput: String = "",
    val isLoading: Boolean = false,
    val recentUrls: List<ShortenedUrl> = emptyList(),
    val errorMessage: String? = null
)

sealed class UrlShortenerEvent {
    data class ShowSnackbar(val message: String) : UrlShortenerEvent()
}
