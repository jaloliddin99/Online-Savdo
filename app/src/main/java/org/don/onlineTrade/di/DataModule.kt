package org.don.onlineTrade.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.don.onlineTrade.data.repository.NetworkRepositoryImpl
import org.don.onlineTrade.domain.repository.NetworkRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {


    @Binds
    @Singleton
    abstract fun bindRepository(
        repositoryImpl: NetworkRepositoryImpl
    ): NetworkRepository

}