package com.example.urlshortener.domain.repository

import com.example.urlshortener.domain.model.Resource
import com.example.urlshortener.domain.model.ShortenedUrl
import kotlinx.coroutines.flow.Flow

interface UrlShortenerRepository {
    suspend fun shortenUrl(url: String): Resource<ShortenedUrl>
    val recentUrls: Flow<List<ShortenedUrl>>
    suspend fun clearHistory()
}
