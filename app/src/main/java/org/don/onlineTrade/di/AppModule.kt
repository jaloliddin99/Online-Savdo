package org.don.onlineTrade.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.don.onlineTrade.data.remote.ApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideRetrofitInstance(
        @ApplicationContext context: Context
    ): ApiInterface{
//        val interceptor = HttpLoggingInterceptor()
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
//        val builder = OkHttpClient.Builder()
//        builder.connectTimeout(20, TimeUnit.SECONDS)
//        builder.addInterceptor(interceptor)
//
//        val chuckerCollector = ChuckerCollector(
//            context = context,
//            showNotification = true,
//            retentionPeriod = RetentionManager.Period.ONE_HOUR
//        )
//
//        val chuckerInterceptor = ChuckerInterceptor.Builder(context)
//            .collector(chuckerCollector)
//            .maxContentLength(500_000L)
//            .redactHeaders("Auth-Token", "Bearer")
//            .alwaysReadResponseBody(true)
//            .build()
//
//
//        builder.addInterceptor(interceptor)
//        builder.addInterceptor(chuckerInterceptor)

        return Retrofit.Builder()
            .baseUrl("http://91.227.40.169:8080/api/v1/")
            //.client(builder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

    }





}