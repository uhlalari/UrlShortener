package com.example.urlshortener.domain.usecase

import android.content.Context
import android.util.Patterns
import com.example.urlshortener.R
import com.example.urlshortener.domain.model.ErrorType
import com.example.urlshortener.domain.model.Resource
import com.example.urlshortener.domain.model.ShortenedUrl
import com.example.urlshortener.domain.repository.UrlShortenerRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ShortenUrlUseCase @Inject constructor(
    private val repository: UrlShortenerRepository,
    @ApplicationContext private val context: Context
) {

    suspend operator fun invoke(
        url: String
    ): Resource<ShortenedUrl> {

        val normalizedUrl = url.trim()

        if (normalizedUrl.isBlank()) {
            return Resource.Error(
                type = ErrorType.VALIDATION,
                message = context.getString(R.string.url_cannot_be_empty)
            )
        }

        val formattedUrl = if (
            normalizedUrl.startsWith("http://") ||
            normalizedUrl.startsWith("https://")
        ) {
            normalizedUrl
        } else {
            "https://$normalizedUrl"
        }

        if (!Patterns.WEB_URL.matcher(formattedUrl).matches()) {
            return Resource.Error(
                type = ErrorType.VALIDATION,
                message = context.getString(R.string.please_enter_a_valid_url)
            )
        }

        return repository.shortenUrl(formattedUrl)
    }
}