package com.example.urlshortener.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ShortenUrlRequest(
    val url: String
)