package com.example.presentation.urlshortener

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performClick
import com.example.urlshortener.domain.model.ShortenedUrl
import com.example.urlshortener.presentation.urlshortener.UrlShortenerContent
import com.example.urlshortener.presentation.urlshortener.UrlShortenerState
import com.example.urlshortener.ui.theme.UrlShortenerTheme
import org.junit.Rule
import org.junit.Test

class UrlShortenerScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun shouldShowEmptyState() {
        composeRule.setContent {
            UrlShortenerTheme {
                UrlShortenerContent(
                    state = UrlShortenerState(),
                    onUrlChange = {},
                    onShortenClick = {},
                    onClearHistoryClick = {},
                    onDismissError = {},
                    onCopyClick = {},
                    onShareClick = {}
                )
            }
        }

        composeRule
            .onNodeWithTag("empty_history")
            .assertIsDisplayed()
    }

    @Test
    fun shouldDisplayUrlWhenTyping() {
        var urlInput by mutableStateOf("")

        composeRule.setContent {
            UrlShortenerTheme {
                UrlShortenerContent(
                    state = UrlShortenerState(urlInput = urlInput),
                    onUrlChange = { urlInput = it },
                    onShortenClick = {},
                    onClearHistoryClick = {},
                    onDismissError = {},
                    onCopyClick = {},
                    onShareClick = {}
                )
            }
        }

        composeRule
            .onNodeWithTag("url_input_field")
            .performTextInput("https://google.com")

        composeRule
            .onNodeWithTag("url_input_field")
            .assertIsDisplayed()
    }

    @Test
    fun shouldShowErrorMessage() {
        composeRule.setContent {
            UrlShortenerTheme {
                UrlShortenerContent(
                    state = UrlShortenerState(errorMessage = "Invalid URL"),
                    onUrlChange = {},
                    onShortenClick = {},
                    onClearHistoryClick = {},
                    onDismissError = {},
                    onCopyClick = {},
                    onShareClick = {}
                )
            }
        }

        composeRule
            .onNodeWithText("Invalid URL")
            .assertIsDisplayed()
    }

    @Test
    fun shouldShowHistory() {
        composeRule.setContent {
            UrlShortenerTheme {
                UrlShortenerContent(
                    state = UrlShortenerState(
                        recentUrls = listOf(
                            ShortenedUrl(
                                alias = "abc",
                                originalUrl = "https://google.com",
                                shortUrl = "https://short.ly/abc"
                            )
                        )
                    ),
                    onUrlChange = {},
                    onShortenClick = {},
                    onClearHistoryClick = {},
                    onDismissError = {},
                    onCopyClick = {},
                    onShareClick = {}
                )
            }
        }

        composeRule
            .onNodeWithText("https://short.ly/abc")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("https://google.com")
            .assertIsDisplayed()
    }

    @Test
    fun shouldShowClearButtonWhenHistoryExists() {
        composeRule.setContent {
            UrlShortenerTheme {
                UrlShortenerContent(
                    state = UrlShortenerState(
                        recentUrls = listOf(
                            ShortenedUrl(
                                alias = "abc",
                                originalUrl = "https://google.com",
                                shortUrl = "https://short.ly/abc"
                            )
                        )
                    ),
                    onUrlChange = {},
                    onShortenClick = {},
                    onClearHistoryClick = {},
                    onDismissError = {},
                    onCopyClick = {},
                    onShareClick = {}
                )
            }
        }

        composeRule
            .onNodeWithTag("clear_history_button")
            .assertIsDisplayed()
            .assertIsEnabled()
            .performClick()
    }
}