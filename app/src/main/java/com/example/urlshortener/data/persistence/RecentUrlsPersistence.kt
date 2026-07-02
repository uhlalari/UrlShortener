package com.example.urlshortener.data.persistence

import android.content.Context
import android.content.SharedPreferences
import com.example.urlshortener.domain.model.ShortenedUrl
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentUrlsPersistence @Inject constructor(
    @ApplicationContext private val context: Context,
    moshi: Moshi
) {

    private val adapter: JsonAdapter<List<ShortenedUrl>> =
        moshi.adapter(
            Types.newParameterizedType(
                List::class.java,
                ShortenedUrl::class.java
            )
        )

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(
            "recent_urls",
            Context.MODE_PRIVATE
        )

    fun saveUrls(urls: List<ShortenedUrl>) {
        val json = adapter.toJson(urls)
        sharedPreferences.edit()
            .putString("urls", json)
            .apply()
    }

    fun loadUrls(): List<ShortenedUrl> {
        val json = sharedPreferences.getString("urls", null)
        return if (json.isNullOrBlank()) {
            emptyList()
        } else {
            adapter.fromJson(json) ?: emptyList()
        }
    }

    fun clearUrls() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }
}