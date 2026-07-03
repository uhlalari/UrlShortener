package com.example.urlshortener.presentation.urlshortener

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urlshortener.R
import com.example.urlshortener.domain.model.ErrorType
import com.example.urlshortener.domain.model.Resource
import com.example.urlshortener.domain.repository.UrlShortenerRepository
import com.example.urlshortener.domain.usecase.ShortenUrlUseCase
import com.example.urlshortener.presentation.urlshortener.UrlShortenerEvent.ShowSnackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UrlShortenerViewModel @Inject constructor(
    private val shortenUrlUseCase: ShortenUrlUseCase,
    private val repository: UrlShortenerRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(UrlShortenerState())
    val state: StateFlow<UrlShortenerState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<UrlShortenerEvent>()
    val events: SharedFlow<UrlShortenerEvent> = _events.asSharedFlow()

    init {
        observeRecentUrls()
    }

    fun onUrlInputChange(url: String) {
        _state.update {
            it.copy(
                urlInput = url,
                errorMessage = null
            )
        }
    }

    fun onShortenUrlClick() {
        val url = state.value.urlInput

        viewModelScope.launch {

            updateLoading(true)

            try {

                val result = withTimeout(REQUEST_TIMEOUT_MILLIS) {
                    shortenUrlUseCase(url)
                }

                when (result) {

                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                urlInput = "",
                                errorMessage = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _state.update {
                            it.copy(errorMessage = result.type.toMessage())
                        }
                    }
                }

            } catch (_: TimeoutCancellationException) {

                Timber.e("Timeout while calling server")

                _state.update {
                    it.copy(errorMessage = context.getString(R.string.request_timeout))
                }

            } finally {

                updateLoading(false)
            }
        }
    }

    fun onClearHistoryClick() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun onLinkCopied() {
        viewModelScope.launch {
            _events.emit(
                ShowSnackbar(context.getString(R.string.url_copied_to_clipboard))
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun observeRecentUrls() {
        viewModelScope.launch {
            repository.recentUrls.collect { urls ->
                _state.update { it.copy(recentUrls = urls) }
            }
        }
    }

    private fun updateLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun ErrorType.toMessage(): String = when (this) {
        ErrorType.EMPTY_URL -> context.getString(R.string.url_cannot_be_empty)
        ErrorType.INVALID_URL -> context.getString(R.string.please_enter_a_valid_url)
        ErrorType.NETWORK -> context.getString(R.string.check_your_internet_connection)
        ErrorType.SERVER -> context.getString(R.string.server_error)
        ErrorType.UNKNOWN -> context.getString(R.string.unexpected_error)
    }

    private companion object {
        const val REQUEST_TIMEOUT_MILLIS = 20_000L
    }
}