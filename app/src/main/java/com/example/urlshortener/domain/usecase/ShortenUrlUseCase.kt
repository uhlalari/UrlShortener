package com.example.urlshortener.domain.usecase

import com.example.urlshortener.domain.model.ErrorType
import com.example.urlshortener.domain.model.Resource
import com.example.urlshortener.domain.model.ShortenedUrl
import com.example.urlshortener.domain.repository.UrlShortenerRepository
import java.net.MalformedURLException
import java.net.URL
import javax.inject.Inject

class ShortenUrlUseCase @Inject constructor(
    private val repository: UrlShortenerRepository
) {

    suspend operator fun invoke(
        url: String
    ): Resource<ShortenedUrl> {

        val normalizedUrl = url.trim()

        if (normalizedUrl.isBlank()) {
            return Resource.Error(ErrorType.EMPTY_URL)
        }

        val formattedUrl = if (
            normalizedUrl.startsWith("http://") ||
            normalizedUrl.startsWith("https://")
        ) {
            normalizedUrl
        } else {
            "https://$normalizedUrl"
        }

        if (!formattedUrl.isValidUrl()) {
            return Resource.Error(ErrorType.INVALID_URL)
        }

        return repository.shortenUrl(formattedUrl)
    }

    private fun String.isValidUrl(): Boolean =
        try {
            val url = URL(this)
            url.host.isNotBlank() && url.host.contains(".")
        } catch (exception: MalformedURLException) {
            false
        }
}