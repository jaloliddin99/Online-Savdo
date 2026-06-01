package uz.promo.selling.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.promo.selling.data.location.DefaultLocationTracker
import uz.promo.selling.data.repository.NetworkRepositoryImpl
import uz.promo.selling.domain.repository.LocationTracker
import uz.promo.selling.domain.repository.NetworkRepository
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