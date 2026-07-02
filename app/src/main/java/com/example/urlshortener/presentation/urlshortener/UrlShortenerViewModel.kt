package com.example.urlshortener.presentation.urlshortener

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urlshortener.R
import com.example.urlshortener.domain.model.Resource
import com.example.urlshortener.domain.repository.UrlShortenerRepository
import com.example.urlshortener.domain.usecase.ShortenUrlUseCase
import com.example.urlshortener.presentation.urlshortener.UrlShortenerEvent.ShowSnackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

                Timber.d("Starting shorten url request")

                val result = withTimeout(20_000L) {
                    withContext(Dispatchers.IO) {
                        shortenUrlUseCase(url)
                    }
                }

                Timber.d("Shorten result: $result")

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
                            it.copy(
                                errorMessage = result.message
                            )
                        }
                    }
                }

            } catch (_: TimeoutCancellationException) {

                Timber.e("Timeout while calling server")

                _state.update {
                    it.copy(
                        errorMessage = "O servidor está demorando para responder. Tente novamente em alguns instantes."
                    )
                }

            } catch (exception: Exception) {

                Timber.e(exception)

                _state.update {
                    it.copy(
                        errorMessage = exception.localizedMessage
                            ?: context.getString(R.string.unexpected_error)
                    )
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
                ShowSnackbar(
                    context.getString(R.string.url_copied_to_clipboard)
                )
            )
        }
    }

    fun clearError() {
        _state.update {
            it.copy(errorMessage = null)
        }
    }

    private fun observeRecentUrls() {
        viewModelScope.launch {
            repository.recentUrls.collect { urls ->
                _state.update {
                    it.copy(recentUrls = urls)
                }
            }
        }
    }

    private fun updateLoading(
        isLoading: Boolean
    ) {
        _state.update {
            it.copy(isLoading = isLoading)
        }
    }
}