/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.di.module

import app.ogasimli.rhino.di.scope.ApplicationScope
import app.ogasimli.rhino.model.data.remote.api.JobsApiService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

/**
 * DI Module providing app-level network services
 *
 * @author Orkhan Gasimli on 14.02.2018.
 */
@Module(includes = [NetworkModule::class])
class NetworkServiceModule {

    @ApplicationScope
    @Provides
    fun provideJobsApiService(retrofit: Retrofit): JobsApiService {
        return retrofit.create(JobsApiService::class.java)
    }
}