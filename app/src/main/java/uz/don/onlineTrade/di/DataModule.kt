package uz.don.onlineTrade.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.don.onlineTrade.data.location.DefaultLocationTracker
import uz.don.onlineTrade.data.repository.NetworkRepositoryImpl
import uz.don.onlineTrade.domain.repository.LocationTracker
import uz.don.onlineTrade.domain.repository.NetworkRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {


    @Binds
    @Singleton
    abstract fun bindRepository(
        repositoryImpl: NetworkRepositoryImpl
    ): NetworkRepository

    @Binds
    @Singleton
    abstract fun bindLocationTracker(
        defaultLocationTracker: DefaultLocationTracker
    ): LocationTracker


}