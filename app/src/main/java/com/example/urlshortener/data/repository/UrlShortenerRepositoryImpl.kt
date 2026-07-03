package com.example.urlshortener.data.repository

import com.example.urlshortener.data.mapper.toDomain
import com.example.urlshortener.data.persistence.RecentUrlsPersistence
import com.example.urlshortener.data.remote.UrlShortenerApi
import com.example.urlshortener.data.remote.dto.ShortenUrlRequest
import com.example.urlshortener.domain.model.ErrorType
import com.example.urlshortener.domain.model.Resource
import com.example.urlshortener.domain.model.ShortenedUrl
import com.example.urlshortener.domain.repository.UrlShortenerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UrlShortenerRepositoryImpl @Inject constructor(
    private val api: UrlShortenerApi,
    private val persistence: RecentUrlsPersistence
) : UrlShortenerRepository {

    private val recentUrlsState =
        MutableStateFlow<List<ShortenedUrl>>(persistence.loadUrls())

    override val recentUrls: Flow<List<ShortenedUrl>>
        get() = recentUrlsState.asStateFlow()

    override suspend fun shortenUrl(
        url: String
    ): Resource<ShortenedUrl> {

        return try {

            val response = api.shortenUrl(ShortenUrlRequest(url))

            val body = response.body()

            if (!response.isSuccessful || body == null) {
                Timber.e("API returned ${response.code()}")
                return Resource.Error(ErrorType.SERVER)
            }

            val shortenedUrl = body.toDomain()

            recentUrlsState.update {
                val updated = listOf(shortenedUrl) + it
                persistence.saveUrls(updated)
                updated
            }

            Timber.d("Short URL created successfully.")

            Resource.Success(shortenedUrl)

        } catch (exception: IOException) {
            Timber.e(exception)
            Resource.Error(ErrorType.NETWORK)
        } catch (exception: HttpException) {
            Timber.e(exception)
            Resource.Error(ErrorType.SERVER)
        } catch (exception: Exception) {
            Timber.e(exception)
            Resource.Error(ErrorType.UNKNOWN)
        }
    }

    override suspend fun clearHistory() {
        recentUrlsState.value = emptyList()
        persistence.clearUrls()
    }
}