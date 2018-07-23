/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.di.module

import android.app.Application
import android.content.Context
import app.ogasimli.remoter.di.qualifier.ApplicationContext
import app.ogasimli.remoter.di.scope.ApplicationScope
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
}