package com.example.urlshortener

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.example.urlshortener.presentation.urlshortener.UrlShortenerScreen
import com.example.urlshortener.ui.theme.UrlShortenerTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UrlShortenerTheme {
                UrlShortenerScreen()
            }
        }
    }
}