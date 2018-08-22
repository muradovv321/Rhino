/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.di.module

import android.app.Application
import android.content.Context
import app.ogasimli.rhino.R
import app.ogasimli.rhino.di.qualifier.ApplicationContext
import app.ogasimli.rhino.di.scope.ApplicationScope
import app.ogasimli.rhino.model.models.FilterKeywords
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

/**
 * DI Module providing app-level dependencies
 *
 * @author Orkhan Gasimli on 16.07.2018.
 */
@Module(includes = [NetworkServiceModule::class, RoomModule::class])
class AppModule {

    /**
     * Provides app context
     */
    @ApplicationScope
    @Provides
    @ApplicationContext
    fun provideAppContext(app: Application): Context = app

    /**
     * Inject global CompositeDisposable
     */
    @Provides
    fun provideCompositeDisposable() = CompositeDisposable()

    /**
     * Inject global CompositeDisposable
     */
    @ApplicationScope
    @Provides
    fun provideFilterKeywords(app: Application): FilterKeywords {
        val data = app.resources.openRawResource(R.raw.filter_keywords)
                .bufferedReader().use { it.readText() }
        return Gson().fromJson(data, FilterKeywords::class.java)
    }
}