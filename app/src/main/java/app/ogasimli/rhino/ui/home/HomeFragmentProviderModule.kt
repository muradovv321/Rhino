/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.ui.home

import app.ogasimli.rhino.di.scope.FragmentScope
import app.ogasimli.rhino.ui.home.fragment.joblist.JobListFragment
import app.ogasimli.rhino.ui.home.fragment.joblist.JobListFragmentModule
import app.ogasimli.rhino.ui.home.fragment.savedjoblist.SavedJobListFragment
import app.ogasimli.rhino.ui.home.fragment.savedjoblist.SavedJobListFragmentModule
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

    @FragmentScope
    @ContributesAndroidInjector(modules = [SavedJobListFragmentModule::class])
    abstract fun provideSavedJobListFragment(): SavedJobListFragment
}