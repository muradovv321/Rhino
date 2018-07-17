/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home

import app.ogasimli.remoter.di.scope.FragmentScope
import app.ogasimli.remoter.ui.home.fragment.JobListFragment
import app.ogasimli.remoter.ui.home.fragment.JobListFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * DI Module used for injection of Fragments
 *
 * @author Orkhan Gasimli on 16.07.2018.
 */
@Module
abstract class HomeFragmentProviderModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [JobListFragmentModule::class])
    abstract fun provideJobListFragment(): JobListFragment
}