package uz.promo.selling.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import uz.promo.selling.BuildConfig
import uz.promo.selling.data.local.AppDatabase
import uz.promo.selling.data.local.SearchHistoryDao
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.data.remote.AuthInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {



    @Provides
    @Singleton
    fun provideRetrofitInstance(
        @ApplicationContext context: Context
    ): ApiInterface{
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(20, TimeUnit.SECONDS)
        // AI endpoints make multiple model calls server-side, so reads can take
        // much longer than the 10s default. Timeouts are maximums — fast calls
        // still return immediately.
        builder.readTimeout(60, TimeUnit.SECONDS)
        builder.writeTimeout(60, TimeUnit.SECONDS)
        builder.addInterceptor(interceptor)

        val chuckerCollector = ChuckerCollector(
            context = context,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )

        val chuckerInterceptor = ChuckerInterceptor.Builder(context)
            .collector(chuckerCollector)
            .maxContentLength(500_000L)
            .redactHeaders("Auth-Token", "Bearer")
            .alwaysReadResponseBody(true)
            .build()

        builder.addInterceptor(interceptor)
        builder.addInterceptor(chuckerInterceptor)
        builder.addInterceptor(AuthInterceptor())

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(builder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

    }
    @Provides
    @Singleton
    fun fusedLocationProviderClient(app: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(app)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "selling_uz_db"
        ).build()
    }

    @Provides
    fun provideSearchHistoryDao(db: AppDatabase): SearchHistoryDao {
        return db.searchHistoryDao()
    }
}