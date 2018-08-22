/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.di.module

import android.content.Context
import app.ogasimli.rhino.BuildConfig
import app.ogasimli.rhino.di.qualifier.ApplicationContext
import app.ogasimli.rhino.di.scope.ApplicationScope
import app.ogasimli.rhino.helper.constant.Constants.API_BASE_URL
import app.ogasimli.rhino.helper.constant.Constants.OK_HTTP_CACHE_FOLDER_NAME
import app.ogasimli.rhino.helper.constant.Constants.OK_HTTP_MAX_AGE
import app.ogasimli.rhino.helper.constant.Constants.OK_HTTP_MAX_CACHE_SIZE
import app.ogasimli.rhino.helper.constant.Constants.OK_HTTP_TIMEOUT
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * DI Module providing app-level dependencies for networking
 *
 * @author Orkhan Gasimli on 14.02.2018.
 */
@Module
class NetworkModule {

    @ApplicationScope
    @Provides
    fun provideRetrofit(@ApplicationContext context: Context): Retrofit =
            Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(provideOkHttpClient(context))
                    .build()

    @ApplicationScope
    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient =
            OkHttpClient.Builder()
                    .addInterceptor(provideHttpLoggingInterceptor())
                    .addNetworkInterceptor(provideCacheInterceptor())
                    .cache(provideCache(context))
                    .connectTimeout(OK_HTTP_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(OK_HTTP_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(OK_HTTP_TIMEOUT, TimeUnit.SECONDS)
                    .build()

    @ApplicationScope
    @Provides
    fun provideCache(@ApplicationContext context: Context): Cache? {
        var cache: Cache? = null
        try {
            cache = Cache(File(context.cacheDir, OK_HTTP_CACHE_FOLDER_NAME),
                    OK_HTTP_MAX_CACHE_SIZE) // 10 MB
        } catch (e: Exception) {
            Timber.e(e, "Could not create Cache!")
        }

        return cache
    }

    @ApplicationScope
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor(
                HttpLoggingInterceptor.Logger { message -> Timber.d(message) })
        httpLoggingInterceptor.level = if (BuildConfig.DEBUG) HEADERS else NONE
        return httpLoggingInterceptor
    }

    @ApplicationScope
    @Provides
    fun provideCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())

            // re-write response header to force use of cache
            val cacheControl = CacheControl.Builder()
                    .maxAge(OK_HTTP_MAX_AGE, TimeUnit.MINUTES)
                    .build()

            response.newBuilder()
                    .header("Cache-Control", cacheControl.toString())
                    .build()
        }
    }
}