package com.example.urlshortener.presentation.urlshortener

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.urlshortener.R

@Composable
fun UrlShortenerScreen(
    viewModel: UrlShortenerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val shortenedUrlLabel = stringResource(R.string.shortened_url)
    val shareLinkLabel = stringResource(R.string.share_link)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is UrlShortenerEvent.ShowSnackbar ->
                    snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    UrlShortenerContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onUrlChange = viewModel::onUrlInputChange,
        onShortenClick = viewModel::onShortenUrlClick,
        onClearHistoryClick = viewModel::onClearHistoryClick,
        onDismissError = viewModel::clearError,
        onCopyClick = { url ->
            context.copyToClipboard(url, shortenedUrlLabel)
            viewModel.onLinkCopied()
        },
        onShareClick = { url ->
            context.shareUrl(url, shareLinkLabel)
        }
    )
}

private fun Context.copyToClipboard(
    text: String,
    label: String
) {
    val clipboard =
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    clipboard.setPrimaryClip(
        ClipData.newPlainText(
            label,
            text
        )
    )
}

private fun Context.shareUrl(
    url: String,
    shareLabel: String
) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, url)
    }

    startActivity(
        Intent.createChooser(
            intent,
            shareLabel
        )
    )
}