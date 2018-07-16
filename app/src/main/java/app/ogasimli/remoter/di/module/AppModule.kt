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
import dagger.Module
import dagger.Provides

/**
 * DI Module providing app-level dependencies
 *
 * @author Orkhan Gasimli on 16.07.2018.
 */
@Module(subcomponents = [] /* Add additional sub components here */)
class AppModule {

    /**
     * Provide app context
     */
    @Provides
    @ApplicationContext
    fun provideAppContext(app: Application): Context = app
}