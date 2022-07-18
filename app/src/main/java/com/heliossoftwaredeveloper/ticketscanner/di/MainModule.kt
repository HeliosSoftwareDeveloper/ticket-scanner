/* (c) Helios Software Developer. All rights reserved. */
package com.heliossoftwaredeveloper.ticketscanner.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.heliossoftwaredeveloper.ticketscanner.BuildConfig
import com.heliossoftwaredeveloper.ticketscanner.location.LocationManager
import com.heliossoftwaredeveloper.ticketscanner.location.LocationManagerImpl
import com.heliossoftwaredeveloper.ticketscanner.network.ApiServices
import com.heliossoftwaredeveloper.ticketscanner.network.RequestInterceptor
import com.heliossoftwaredeveloper.ticketscanner.network.source.RemoteSource
import com.heliossoftwaredeveloper.ticketscanner.network.source.RemoteSourceImpl
import com.heliossoftwaredeveloper.ticketscanner.repository.VenueRepository
import com.heliossoftwaredeveloper.ticketscanner.repository.VenueRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val TIMEOUT_DURATION = 30L

@Module
@InstallIn(SingletonComponent::class)
class MainModule {
    @Provides
    @Singleton
    fun provideLocationManager(): LocationManager = LocationManagerImpl()

    @Provides
    @Singleton
    fun providesOkHttpClient(
        authenticatedInterceptor: Interceptor,
        requestInterceptor: RequestInterceptor
    ): OkHttpClient {

        val builder = OkHttpClient.Builder()
            .apply {
                addInterceptor(requestInterceptor)
                connectTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
                readTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
                writeTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.level = HttpLoggingInterceptor.Level.BODY
                    logging.redactHeader("Authorization")
                    logging.redactHeader("Cookie")
                    addInterceptor(logging)
                }
            }

        builder.addInterceptor(authenticatedInterceptor)

        return builder
            .build()
    }

    @Singleton
    @Provides
    fun providesCustomHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val requestBuilder = request.newBuilder().apply {
                addHeader("Accept", "application/json")
                if (request.url.toString().contains(BuildConfig.BASE_URL)) {
                    addHeader("Content-Type", "application/json")
                } else {
                    addHeader("Content-Type", "application/x-www-form-urlencoded")
                }
            }.build()
            return@Interceptor chain.proceed(requestBuilder)
        }
    }

    @Singleton
    @Provides
    fun providesRequestInterceptor(): RequestInterceptor {
        return RequestInterceptor()
    }

    @Provides
    @Singleton
    fun providesGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun providesScalarsConverterFactory(): ScalarsConverterFactory {
        return ScalarsConverterFactory.create()
    }

    @Provides
    @Singleton
    fun providesRxJava3CallAdapterFactory(): RxJava3CallAdapterFactory {
        return RxJava3CallAdapterFactory.create()
    }

    @Provides
    @Singleton
    fun providesGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun providesRetrofit(
        okHttpClient: OkHttpClient,
        scalarFactory: ScalarsConverterFactory,
        gsonConverterFactory: GsonConverterFactory,
        rxJava2Factory: RxJava3CallAdapterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(scalarFactory)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(rxJava2Factory)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providesApiServices(retrofit: Retrofit) = retrofit.create(ApiServices::class.java)

    @Provides
    @Singleton
    fun RemoteSource(
        apiServices: ApiServices
    ): RemoteSource = RemoteSourceImpl(apiServices)

    @Provides
    @Singleton
    fun VenueRepository(
        remote: RemoteSource
    ): VenueRepository = VenueRepositoryImpl(remote)
}