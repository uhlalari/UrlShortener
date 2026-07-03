package com.example.urlshortener.di

import com.example.urlshortener.data.repository.UrlShortenerRepositoryImpl
import com.example.urlshortener.domain.repository.UrlShortenerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUrlShortenerRepository(
        impl: UrlShortenerRepositoryImpl
    ): UrlShortenerRepository
}