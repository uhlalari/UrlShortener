package com.example.urlshortener.domain.model

data class ShortenedUrl(
    val alias: String,
    val originalUrl: String,
    val shortUrl: String,
    val createdAt: Long = System.currentTimeMillis()
)