/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.di.component

import android.app.Application
import app.ogasimli.rhino.app.RhinoApp
import app.ogasimli.rhino.di.module.ActivityBuilderModule
import app.ogasimli.rhino.di.module.AppModule
import app.ogasimli.rhino.di.module.GlideModule
import app.ogasimli.rhino.di.module.ViewModelModule
import app.ogasimli.rhino.di.scope.ApplicationScope
import dagger.BindsInstance
import dagger.Component
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
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: RhinoApp)

    fun inject(glide: GlideModule)
}