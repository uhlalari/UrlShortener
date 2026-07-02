package com.example.urlshortener.presentation.urlshortener

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.urlshortener.R
import com.example.urlshortener.presentation.urlshortener.components.ErrorCard
import com.example.urlshortener.presentation.urlshortener.components.HistorySection
import com.example.urlshortener.presentation.urlshortener.components.UrlInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlShortenerContent(
    state: UrlShortenerState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onUrlChange: (String) -> Unit,
    onShortenClick: () -> Unit,
    onClearHistoryClick: () -> Unit,
    onDismissError: () -> Unit,
    onCopyClick: (String) -> Unit,
    onShareClick: (String) -> Unit
) {

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.url_shortener))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            UrlInput(
                urlInput = state.urlInput,
                isLoading = state.isLoading,
                onUrlChange = onUrlChange,
                onShortenClick = onShortenClick
            )

            if (state.errorMessage != null) {

                Spacer(modifier = Modifier.height(16.dp))

                ErrorCard(
                    message = state.errorMessage,
                    onDismiss = onDismissError
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            HistorySection(
                recentUrls = state.recentUrls,
                onClearHistory = onClearHistoryClick,
                onCopy = onCopyClick,
                onShare = onShareClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}