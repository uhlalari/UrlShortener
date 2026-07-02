package com.example.urlshortener.domain.usecase

import android.content.Context
import com.example.urlshortener.R
import com.example.urlshortener.domain.model.ErrorType
import com.example.urlshortener.domain.model.Resource
import com.example.urlshortener.domain.model.ShortenedUrl
import com.example.urlshortener.domain.repository.UrlShortenerRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ShortenUrlUseCaseTest {

    private lateinit var repository: UrlShortenerRepository
    private lateinit var context: Context
    private lateinit var useCase: ShortenUrlUseCase

    @Before
    fun setup() {
        repository = mockk()
        context = mockk()
        every { context.getString(R.string.url_cannot_be_empty) } returns "URL cannot be empty."
        every { context.getString(R.string.please_enter_a_valid_url) } returns "Please enter a valid URL."
        useCase = ShortenUrlUseCase(repository, context)
    }

    @Test
    fun `should shorten valid url`() = runTest {

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
    fun `should trim spaces`() = runTest {

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
    fun `should add https when protocol is missing`() = runTest {

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
    fun `should return validation error when url is blank`() = runTest {

        val result = useCase("")

        assertTrue(result is Resource.Error)

        val error = result as Resource.Error

        assertEquals(ErrorType.VALIDATION, error.type)
        assertEquals("URL cannot be empty.", error.message)
    }

    @Test
    fun `should return validation error when url is invalid`() = runTest {

        val result = useCase("abc")

        assertTrue(result is Resource.Error)

        val error = result as Resource.Error

        assertEquals(ErrorType.VALIDATION, error.type)
        assertEquals("Please enter a valid URL.", error.message)
    }

    @Test
    fun `should accept url with query parameters`() = runTest {

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
    fun `should propagate repository error`() = runTest {

        coEvery {
            repository.shortenUrl(any())
        } returns Resource.Error(
            ErrorType.NETWORK,
            "No internet connection"
        )

        val result = useCase("https://google.com")

        assertTrue(result is Resource.Error)

        val error = result as Resource.Error

        assertEquals(ErrorType.NETWORK, error.type)
        assertEquals("No internet connection", error.message)
    }
}