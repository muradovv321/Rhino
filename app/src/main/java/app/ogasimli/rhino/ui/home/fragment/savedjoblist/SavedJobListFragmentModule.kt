/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.ui.home.fragment.savedjoblist

import app.ogasimli.rhino.di.scope.FragmentScope
import app.ogasimli.rhino.ui.home.fragment.adapter.JobsAdapter
import dagger.Module
import dagger.Provides

/**
 * DI Module providing dependencies for SavedJobListFragment
 *
 * @author Orkhan Gasimli on 26.07.2018.
 */
@Module
class SavedJobListFragmentModule {

    @FragmentScope
    @Provides
    fun provideJobsAdapter() = JobsAdapter()
}