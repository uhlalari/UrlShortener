package com.example.urlshortener.presentation

import android.content.Context
import com.example.urlshortener.R
import com.example.urlshortener.domain.model.ErrorType
import com.example.urlshortener.domain.model.Resource
import com.example.urlshortener.domain.model.ShortenedUrl
import com.example.urlshortener.domain.repository.UrlShortenerRepository
import com.example.urlshortener.domain.usecase.ShortenUrlUseCase
import com.example.urlshortener.presentation.urlshortener.UrlShortenerEvent
import com.example.urlshortener.presentation.urlshortener.UrlShortenerViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UrlShortenerViewModelTest {

    private lateinit var viewModel: UrlShortenerViewModel
    private lateinit var useCase: ShortenUrlUseCase
    private lateinit var repository: UrlShortenerRepository
    private lateinit var context: Context
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        useCase = mockk()
        repository = mockk()
        context = mockk()

        every {
            context.getString(R.string.url_cannot_be_empty)
        } returns "URL cannot be empty"

        every {
            context.getString(R.string.please_enter_a_valid_url)
        } returns "Please enter a valid URL"

        every {
            context.getString(R.string.check_your_internet_connection)
        } returns "Check your internet connection"

        every {
            context.getString(R.string.server_error)
        } returns "Server error"

        every {
            context.getString(R.string.unexpected_error)
        } returns "Unexpected error"

        every {
            context.getString(R.string.request_timeout)
        } returns "Request timeout"

        every {
            context.getString(R.string.url_copied_to_clipboard)
        } returns "URL copied to clipboard"

        coEvery { repository.recentUrls } returns flowOf(emptyList())

        viewModel = UrlShortenerViewModel(useCase, repository, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun shouldShowLoadingWhenShorteningUrl() = runTest(testDispatcher) {
        val shortenedUrl = ShortenedUrl(
            alias = "abc",
            originalUrl = "https://google.com",
            shortUrl = "https://short.ly/abc"
        )

        // Suspende indefinidamente para manter loading true
        coEvery { useCase(any()) } coAnswers {
            kotlinx.coroutines.delay(Long.MAX_VALUE)
            Resource.Success(shortenedUrl)
        }

        viewModel.onUrlInputChange("https://google.com")
        viewModel.onShortenUrlClick()

        // Executa até o loading ser atualizado
        runCurrent()

        assertTrue(viewModel.state.value.isLoading)
    }

    @Test
    fun shouldClearInputOnSuccess() = runTest(testDispatcher) {
        val shortenedUrl = ShortenedUrl(
            alias = "abc",
            originalUrl = "https://google.com",
            shortUrl = "https://short.ly/abc"
        )

        coEvery { useCase(any()) } returns Resource.Success(shortenedUrl)

        val urlsFlow = MutableStateFlow<List<ShortenedUrl>>(emptyList())
        coEvery { repository.recentUrls } returns urlsFlow

        viewModel.onUrlInputChange("https://google.com")
        viewModel.onShortenUrlClick()

        advanceUntilIdle()

        assertEquals("", viewModel.state.value.urlInput)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun shouldShowErrorMessageOnValidationError() = runTest(testDispatcher) {
        coEvery { useCase(any()) } returns Resource.Error(ErrorType.EMPTY_URL)

        viewModel.onUrlInputChange("")
        viewModel.onShortenUrlClick()

        advanceUntilIdle()

        assertEquals("URL cannot be empty", viewModel.state.value.errorMessage)
    }

    @Test
    fun shouldShowErrorMessageOnNetworkError() = runTest(testDispatcher) {
        coEvery { useCase(any()) } returns Resource.Error(ErrorType.NETWORK)

        viewModel.onUrlInputChange("https://google.com")
        viewModel.onShortenUrlClick()

        advanceUntilIdle()

        assertEquals("Check your internet connection", viewModel.state.value.errorMessage)
    }

    @Test
    fun shouldShowErrorMessageOnServerError() = runTest(testDispatcher) {
        coEvery { useCase(any()) } returns Resource.Error(ErrorType.SERVER)

        viewModel.onUrlInputChange("https://google.com")
        viewModel.onShortenUrlClick()

        advanceUntilIdle()

        assertEquals("Server error", viewModel.state.value.errorMessage)
    }

    @Test
    fun shouldShowTimeoutError() = runTest(testDispatcher) {
        coEvery { useCase(any()) } returns Resource.Error(ErrorType.UNKNOWN)

        viewModel.onUrlInputChange("https://google.com")
        viewModel.onShortenUrlClick()

        advanceUntilIdle()

        assertEquals("Unexpected error", viewModel.state.value.errorMessage)
    }

    @Test
    fun shouldClearErrorMessage() = runTest(testDispatcher) {
        coEvery { useCase(any()) } returns Resource.Error(ErrorType.EMPTY_URL)

        viewModel.onUrlInputChange("")
        viewModel.onShortenUrlClick()

        advanceUntilIdle()

        viewModel.clearError()

        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun shouldClearHistory() = runTest(testDispatcher) {
        coEvery { repository.clearHistory() } returns Unit

        viewModel.onClearHistoryClick()

        advanceUntilIdle()

        coVerify(exactly = 1) { repository.clearHistory() }
    }

    @Test
    fun shouldEmitSnackbarEventOnCopy() = runTest(testDispatcher) {
        viewModel.onLinkCopied()

        val event = viewModel.events.first()
        assertTrue(event is UrlShortenerEvent.ShowSnackbar)
        assertEquals("URL copied to clipboard", (event as UrlShortenerEvent.ShowSnackbar).message)
    }

    @Test
    fun shouldClearErrorWhenUrlChanges() = runTest(testDispatcher) {
        coEvery { useCase(any()) } returns Resource.Error(ErrorType.EMPTY_URL)

        viewModel.onUrlInputChange("")
        viewModel.onShortenUrlClick()

        advanceUntilIdle()

        viewModel.onUrlInputChange("https://google.com")

        assertNull(viewModel.state.value.errorMessage)
    }
}