package com.example.urlshortener.data.repository

import com.example.urlshortener.data.remote.UrlShortenerApi
import com.example.urlshortener.data.remote.dto.LinksDto
import com.example.urlshortener.data.remote.dto.ShortenUrlResponse
import com.example.urlshortener.domain.model.ErrorType
import com.example.urlshortener.domain.model.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class UrlShortenerRepositoryImplTest {

    private lateinit var api: UrlShortenerApi

    private lateinit var repository: UrlShortenerRepositoryImpl

    @Before
    fun setup() {

        api = mockk()

        repository = UrlShortenerRepositoryImpl(api)
    }

    @Test
    fun `should shorten url successfully`() = runTest {

        val response = ShortenUrlResponse(
            alias = "abc123",
            links = LinksDto(
                self = "https://google.com",
                short = "https://short.ly/abc123"
            )
        )

        coEvery {
            api.shortenUrl(any())
        } returns Response.success(response)

        val result =
            repository.shortenUrl("https://google.com")

        assertTrue(result is Resource.Success)

        val success = result as Resource.Success

        assertEquals(
            "abc123",
            success.data.alias
        )

        assertEquals(
            1,
            repository.recentUrls.first().size
        )
    }

    @Test
    fun `should return server error`() = runTest {

        val errorBody =
            "".toResponseBody(
                "application/json".toMediaType()
            )

        coEvery {
            api.shortenUrl(any())
        } returns Response.error(
            500,
            errorBody
        )

        val result =
            repository.shortenUrl("https://google.com")

        assertTrue(result is Resource.Error)

        val error = result as Resource.Error

        assertEquals(
            ErrorType.SERVER,
            error.type
        )
    }

    @Test
    fun `should return network error`() = runTest {

        coEvery {
            api.shortenUrl(any())
        } throws IOException()

        val result =
            repository.shortenUrl("https://google.com")

        assertTrue(result is Resource.Error)

        val error = result as Resource.Error

        assertEquals(
            ErrorType.NETWORK,
            error.type
        )
    }

    @Test
    fun `should clear history`() = runTest {

        val response = ShortenUrlResponse(
            alias = "abc123",
            links = LinksDto(
                self = "https://google.com",
                short = "https://short.ly/abc123"
            )
        )

        coEvery {
            api.shortenUrl(any())
        } returns Response.success(response)

        repository.shortenUrl("https://google.com")

        repository.clearHistory()

        val history =
            repository.recentUrls.first()

        assertTrue(history.isEmpty())
    }
}
