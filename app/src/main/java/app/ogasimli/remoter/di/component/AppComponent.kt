/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.di.component

import app.ogasimli.remoter.app.RemoterApp
import app.ogasimli.remoter.di.module.ActivityBuilderModule
import app.ogasimli.remoter.di.module.AppModule
import app.ogasimli.remoter.di.module.ViewModelModule
import app.ogasimli.remoter.di.scope.ApplicationScope
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

/**
 * DI Component providing app-level dependencies
 *
 * @author Orkhan Gasimli on 16.07.2018.
 */
@ApplicationScope
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    ActivityBuilderModule::class,
    ViewModelModule::class])
interface AppComponent: AndroidInjector<RemoterApp> {

    @Component.Builder
    abstract class Builder: AndroidInjector.Builder<RemoterApp>()
}