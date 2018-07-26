/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home

import androidx.fragment.app.FragmentManager
import app.ogasimli.remoter.di.scope.ActivityScope
import dagger.Module
import dagger.Provides

/**
 * DI Module providing dependencies for HomeActivity
 *
 * @author Orkhan Gasimli on 16.07.2018.
 */
@Module
class HomeActivityModule {

    @ActivityScope
    @Provides
    fun provideFragmentManager(homeActivity: HomeActivity) = homeActivity.supportFragmentManager

    @ActivityScope
    @Provides
    fun providePagerAdapter(fragmentManager: FragmentManager) = HomePagerAdapter(fragmentManager)
}