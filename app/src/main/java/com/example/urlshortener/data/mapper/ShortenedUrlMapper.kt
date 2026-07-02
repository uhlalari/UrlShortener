package com.example.urlshortener.data.mapper

import com.example.urlshortener.data.remote.dto.ShortenUrlResponse
import com.example.urlshortener.domain.model.ShortenedUrl

fun ShortenUrlResponse.toDomain(): ShortenedUrl =
    ShortenedUrl(
        alias = alias,
        originalUrl = links.self,
        shortUrl = links.short
    )