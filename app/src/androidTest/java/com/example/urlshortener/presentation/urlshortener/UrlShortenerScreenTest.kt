package com.example.urlshortener.presentation.urlshortener

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
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
    fun should_show_empty_state() {

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
            .onNodeWithText("No shortened URLs yet.")
            .assertIsDisplayed()
    }

    @Test
    fun should_display_url_when_typing() {

        var value = ""

        composeRule.setContent {

            UrlShortenerTheme {

                UrlShortenerContent(
                    state = UrlShortenerState(
                        urlInput = value
                    ),
                    onUrlChange = {
                        value = it
                    },
                    onShortenClick = {},
                    onClearHistoryClick = {},
                    onDismissError = {},
                    onCopyClick = {},
                    onShareClick = {}
                )

            }

        }

        composeRule
            .onNode(hasSetTextAction())
            .performTextInput("https://google.com")
    }

    @Test
    fun should_show_error_message() {

        composeRule.setContent {

            UrlShortenerTheme {

                UrlShortenerContent(
                    state = UrlShortenerState(
                        errorMessage = "Invalid URL"
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
            .onNodeWithText("Invalid URL")
            .assertIsDisplayed()
    }

    @Test
    fun should_show_history() {

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
    fun should_show_clear_button_when_history_exists() {

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
            .onNodeWithText("Clear")
            .assertIsDisplayed()
            .assertIsEnabled()
            .performClick()
    }
}
