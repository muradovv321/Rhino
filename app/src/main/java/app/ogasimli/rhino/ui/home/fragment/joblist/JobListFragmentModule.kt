/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.ui.home.fragment.joblist

import app.ogasimli.rhino.di.scope.FragmentScope
import app.ogasimli.rhino.ui.home.fragment.adapter.JobsAdapter
import dagger.Module
import dagger.Provides

/**
 * DI Module providing dependencies for JobListFragment
 *
 * @author Orkhan Gasimli on 16.07.2018.
 */
@Module
class JobListFragmentModule {

    @FragmentScope
    @Provides
    fun provideJobsAdapter() = JobsAdapter()
}