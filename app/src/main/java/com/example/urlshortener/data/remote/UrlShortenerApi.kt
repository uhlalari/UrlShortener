package com.example.urlshortener.data.remote

import com.example.urlshortener.data.remote.dto.ShortenUrlRequest
import com.example.urlshortener.data.remote.dto.ShortenUrlResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UrlShortenerApi {

    @POST("alias")
    suspend fun shortenUrl(
        @Body request: ShortenUrlRequest
    ): Response<ShortenUrlResponse>
}