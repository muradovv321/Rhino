/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.di.module

import app.ogasimli.remoter.di.scope.ActivityScope
import app.ogasimli.remoter.ui.home.HomeActivity
import app.ogasimli.remoter.ui.home.HomeActivityModule
import app.ogasimli.remoter.ui.home.HomeFragmentProviderModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * DI Module used for injection of Activities
 *
 * @author Orkhan Gasimli on 16.07.2018.
 */
@Module
abstract class ActivityBuilderModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [
        HomeActivityModule::class,
        HomeFragmentProviderModule::class])
    abstract fun provideHomeActivity(): HomeActivity
}