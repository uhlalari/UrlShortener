package com.example.urlshortener.domain.usecase

import com.example.urlshortener.domain.model.ErrorType
import com.example.urlshortener.domain.model.Resource
import com.example.urlshortener.domain.model.ShortenedUrl
import com.example.urlshortener.domain.repository.UrlShortenerRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ShortenUrlUseCaseTest {

    private lateinit var repository: UrlShortenerRepository
    private lateinit var useCase: ShortenUrlUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ShortenUrlUseCase(repository)
    }

    @Test
    fun shouldShortenValidUrl() = runTest {
        val expected = ShortenedUrl(
            alias = "abc123",
            originalUrl = "https://google.com",
            shortUrl = "https://short.ly/abc123"
        )

        coEvery {
            repository.shortenUrl("https://google.com")
        } returns Resource.Success(expected)

        val result = useCase("https://google.com")

        assertTrue(result is Resource.Success)
        assertEquals(expected, (result as Resource.Success).data)

        coVerify(exactly = 1) {
            repository.shortenUrl("https://google.com")
        }
    }

    @Test
    fun shouldTrimSpaces() = runTest {
        val expected = ShortenedUrl(
            alias = "abc123",
            originalUrl = "https://google.com",
            shortUrl = "https://short.ly/abc123"
        )

        coEvery {
            repository.shortenUrl("https://google.com")
        } returns Resource.Success(expected)

        useCase("   https://google.com   ")

        coVerify {
            repository.shortenUrl("https://google.com")
        }
    }

    @Test
    fun shouldAddHttpsWhenProtocolIsMissing() = runTest {
        val expected = ShortenedUrl(
            alias = "abc123",
            originalUrl = "https://google.com",
            shortUrl = "https://short.ly/abc123"
        )

        coEvery {
            repository.shortenUrl("https://google.com")
        } returns Resource.Success(expected)

        useCase("google.com")

        coVerify {
            repository.shortenUrl("https://google.com")
        }
    }

    @Test
    fun shouldReturnEmptyUrlErrorWhenUrlIsBlank() = runTest {
        val result = useCase("")

        assertTrue(result is Resource.Error)
        assertEquals(ErrorType.EMPTY_URL, (result as Resource.Error).type)
    }

    @Test
    fun shouldReturnInvalidUrlErrorWhenUrlIsInvalid() = runTest {
        val result = useCase("abc")

        assertTrue(result is Resource.Error)
        assertEquals(ErrorType.INVALID_URL, (result as Resource.Error).type)
    }

    @Test
    fun shouldAcceptUrlWithQueryParameters() = runTest {
        val expected = ShortenedUrl(
            alias = "abc123",
            originalUrl = "https://instagram.com/reel/abc?igsh=test",
            shortUrl = "https://short.ly/abc123"
        )

        coEvery {
            repository.shortenUrl("https://instagram.com/reel/abc?igsh=test")
        } returns Resource.Success(expected)

        val result = useCase("https://instagram.com/reel/abc?igsh=test")

        assertTrue(result is Resource.Success)
        assertEquals(expected, (result as Resource.Success).data)

        coVerify(exactly = 1) {
            repository.shortenUrl("https://instagram.com/reel/abc?igsh=test")
        }
    }

    @Test
    fun shouldPropagateRepositoryError() = runTest {
        coEvery {
            repository.shortenUrl(any())
        } returns Resource.Error(ErrorType.NETWORK)

        val result = useCase("https://google.com")

        assertTrue(result is Resource.Error)
        assertEquals(ErrorType.NETWORK, (result as Resource.Error).type)
    }
}