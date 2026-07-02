package com.example.urlshortener.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ShortenUrlResponse(

    val alias: String,

    @Json(name = "_links")
    val links: LinksDto
)

@JsonClass(generateAdapter = true)
data class LinksDto(

    val self: String,

    val short: String
)