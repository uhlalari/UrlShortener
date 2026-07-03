package com.example.urlshortener.presentation.urlshortener.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.testTag
import com.example.urlshortener.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.urlshortener.domain.model.ShortenedUrl

@Composable
fun HistorySection(
    recentUrls: List<ShortenedUrl>,
    onClearHistory: () -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {

        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = stringResource(R.string.recent_urls),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (recentUrls.isNotEmpty()) {
                TextButton(
                    onClick = onClearHistory,
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Text(stringResource(R.string.clear))
                }
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (recentUrls.isEmpty()) {

            EmptyHistory()

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(
                    items = recentUrls,
                    key = { it.alias }
                ) { url ->

                    UrlCard(
                        shortenedUrl = url,
                        onCopy = onCopy,
                        onShare = onShare
                    )

                }

            }

        }

    }

}

@Composable
private fun EmptyHistory() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("empty_history"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Text(
            text = stringResource(R.string.no_shortened_urls_yet),
            modifier = Modifier.padding(20.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

    }

}